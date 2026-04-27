package wlsh.project.intervai.answer.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.answer.application.AnswerResultGenerator;
import wlsh.project.intervai.answer.application.dto.AnswerResultDto;
import wlsh.project.intervai.answer.domain.Answer;
import wlsh.project.intervai.common.ai.AiChatCaller;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.question.domain.Question;

@Component
@Profile("prod")
public class OllamaAnswerResultGenerator implements AnswerResultGenerator {

    private final AiChatCaller aiChatCaller;
    private final AnswerPromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;

    public OllamaAnswerResultGenerator(AiChatCaller aiChatCaller,
                                       AnswerPromptBuilder promptBuilder,
                                       ObjectMapper objectMapper) {
        this.aiChatCaller = aiChatCaller;
        this.promptBuilder = promptBuilder;
        this.objectMapper = objectMapper;
    }

    @Override
    public AnswerResultDto generate(String sessionId, Interview interview, Question question, Answer answer) {
        String prompt = promptBuilder.build(question, answer);
        String response = aiChatCaller.callWithSession(sessionId, prompt);
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
