package wlsh.project.intervai.answer.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.answer.application.AnswerResultGenerator;
import wlsh.project.intervai.answer.application.dto.AnswerResultDto;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.interview.domain.Interview;

import java.util.Map;

@Component
@Profile("prod")
public
class ClaudeAnswerResultGenerator implements AnswerResultGenerator {

    private final ChatClient chatClient;
    private final Resource promptResource;
    private final ObjectMapper objectMapper;

    public ClaudeAnswerResultGenerator(ChatClient chatClient,
                                       @Value("classpath:prompts/feedback-followup.st") Resource promptResource,
                                       ObjectMapper objectMapper) {
        this.chatClient = chatClient;
        this.promptResource = promptResource;
        this.objectMapper = objectMapper;
    }

    @Override
    public AnswerResultDto generate(String conversationId, Interview interview, String question, String answer) {
        PromptTemplate template = new PromptTemplate(promptResource);
        String userPrompt = template.render(Map.of(
                "question", question,
                "answerText", answer
        ));

        String response = chatClient.prompt()
                .advisors(advisor -> advisor.param("chat_memory_conversation_id", conversationId))
                .user(userPrompt)
                .call()
                .content();

        return parseFeedbackResult(response);
    }

    private AnswerResultDto parseFeedbackResult(String response) {
        try {
            return objectMapper.readValue(response.trim(), AnswerResultDto.class);
        } catch (JsonProcessingException e) {
            return new AnswerResultDto(response.trim(), "");
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
