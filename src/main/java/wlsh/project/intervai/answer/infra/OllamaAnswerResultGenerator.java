package wlsh.project.intervai.answer.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.answer.application.AnswerResultGenerator;
import wlsh.project.intervai.answer.application.dto.AnswerResultDto;
import wlsh.project.intervai.answer.domain.Answer;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.question.domain.Question;

import java.util.Map;

@Component
@Profile("prod")
public class OllamaAnswerResultGenerator implements AnswerResultGenerator {

    private final ChatClient chatClient;
    private final Resource promptResource;
    private final ObjectMapper objectMapper;

    public OllamaAnswerResultGenerator(@Qualifier("ollamaChatClient") ChatClient chatClient,
                                       @Value("classpath:prompts/feedback-followup.st") Resource promptResource,
                                       ObjectMapper objectMapper) {
        this.chatClient = chatClient;
        this.promptResource = promptResource;
        this.objectMapper = objectMapper;
    }

    @Override
    public AnswerResultDto generate(String sessionId, Interview interview, Question question, Answer answer) {
        PromptTemplate template = new PromptTemplate(promptResource);
        String userPrompt = template.render(Map.of(
                "question", question.getContent(),
                "answerText", answer.getContent()
        ));

        String response = chatClient.prompt()
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, sessionId))
                .user(userPrompt)
                .call()
                .content();

        return parseFeedbackResult(response);
    }

    private AnswerResultDto parseFeedbackResult(String response) {
        try {
            return objectMapper.readValue(response.trim(), AnswerResultDto.class);
        } catch (JsonProcessingException e) {
            return new AnswerResultDto(response.trim(), 0, "");
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
