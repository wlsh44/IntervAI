package wlsh.project.intervai.answer.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.answer.application.dto.AnswerResultDto;
import wlsh.project.intervai.answer.domain.Answer;
import wlsh.project.intervai.interview.application.InterviewFinder;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.question.application.QuestionFinder;
import wlsh.project.intervai.question.domain.Question;

@Component
@RequiredArgsConstructor
public class AnswerHandler {

    private final AnswerResultGenerator answerResultGenerator;
    private final InterviewFinder interviewFinder;
    private final QuestionFinder questionFinder;

    public AnswerResultDto submit(Long questionId, Answer answer) {
        Question question = questionFinder.find(questionId);
        Interview interview = interviewFinder.find(answer.getInterviewId());

        return answerResultGenerator.generate(
                String.valueOf(answer.getSessionId()),
                interview,
                question.getContent(),
                answer.getContent()
        );
    }
}