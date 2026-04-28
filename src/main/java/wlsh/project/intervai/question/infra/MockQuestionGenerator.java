package wlsh.project.intervai.question.infra;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.question.application.QuestionGenerator;

@Slf4j
@Component
@Profile("!prod")
public class MockQuestionGenerator implements QuestionGenerator {

    private final QuestionPromptBuilder promptBuilder;

    public MockQuestionGenerator(QuestionPromptBuilder promptBuilder) {
        this.promptBuilder = promptBuilder;
    }

    @Override
    public List<String> generateAll(Interview interview) {
        log.debug("[MockQuestionGenerator.generateAll] 질문 생성 시작 - interviewType={}, difficulty={}, count={}",
                interview.getInterviewType(), interview.getDifficulty(), interview.getQuestionCount());
        promptBuilder.build(interview);
        List<String> questions = new ArrayList<>();
        for (int i = 0; i < interview.getQuestionCount(); i++) {
            questions.add("[Mock] " + interview.getInterviewType().getKo()
                    + " 면접 질문 " + (i + 1) + "번입니다. 난이도: " + interview.getDifficulty().getKo());
        }
        return questions;
    }
}
