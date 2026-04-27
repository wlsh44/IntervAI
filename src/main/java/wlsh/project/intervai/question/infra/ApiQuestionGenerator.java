package wlsh.project.intervai.question.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.common.ai.AiChatCaller;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.question.application.QuestionGenerator;

import java.util.List;

@Component
@Profile("prod")
public class ApiQuestionGenerator implements QuestionGenerator {

    private final AiChatCaller aiChatCaller;
    private final QuestionPromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;

    public ApiQuestionGenerator(AiChatCaller aiChatCaller,
                                QuestionPromptBuilder promptBuilder,
                                ObjectMapper objectMapper) {
        this.aiChatCaller = aiChatCaller;
        this.promptBuilder = promptBuilder;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<String> generateAll(Interview interview) {
        String prompt = promptBuilder.build(interview);
        String response = aiChatCaller.call(prompt);
        return parseQuestions(response);
    }

    private List<String> parseQuestions(String response) {
        try {
            return objectMapper.readValue(response.trim(), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            return List.of(response.trim());
        }
    }
}
