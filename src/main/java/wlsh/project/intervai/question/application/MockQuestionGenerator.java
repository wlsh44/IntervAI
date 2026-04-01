package wlsh.project.intervai.question.application;

import org.springframework.stereotype.Component;
import wlsh.project.intervai.interview.domain.Interview;

@Component
public class MockQuestionGenerator implements QuestionGenerator {

    @Override
    public String generate(Interview interview) {
        return "[Mock] " + interview.getInterviewType().name() + " 면접 질문입니다. "
                + "난이도: " + interview.getDifficulty().name();
    }
}