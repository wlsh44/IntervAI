package wlsh.project.intervai.question.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.common.ai.AiChatCaller;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.question.application.QuestionGenerator;

import java.util.List;

@Slf4j
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
        log.info("[ApiQuestionGenerator.generateAll] 질문 생성 시작 - interviewType={}, difficulty={}, count={}",
                interview.getInterviewType(), interview.getDifficulty(), interview.getQuestionCount());
        String prompt = promptBuilder.build(interview);
        String response = aiChatCaller.call(prompt);
        List<String> questions = parseQuestions(response);
        log.info("[ApiQuestionGenerator.generateAll] 질문 {}개 생성 완료", questions.size());
        return questions;
    }

    private List<String> parseQuestions(String response) {
        try {
            return objectMapper.readValue(response.trim(), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.warn("[ApiQuestionGenerator.parseQuestions] JSON 파싱 실패, 원본 응답을 단일 질문으로 반환");
            return List.of(response.trim());
        }
    }
}
