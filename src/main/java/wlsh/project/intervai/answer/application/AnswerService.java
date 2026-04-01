package wlsh.project.intervai.answer.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wlsh.project.intervai.answer.domain.Answer;
import wlsh.project.intervai.question.application.QuestionFinder;
import wlsh.project.intervai.question.domain.Question;
import wlsh.project.intervai.session.application.InterviewSessionFinder;
import wlsh.project.intervai.session.domain.InterviewSession;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final InterviewSessionFinder interviewSessionFinder;
    private final QuestionFinder questionFinder;
    private final AnswerValidator answerValidator;
    private final AnswerManager answerManager;

    public Answer create(Long userId, Long questionId, String content) {
        Question question = questionFinder.find(questionId);
        InterviewSession session = interviewSessionFinder.find(question.getSessionId());
        answerValidator.validate(session, questionId);
        return answerManager.create(userId, session.getInterviewId(),
                session.getId(), questionId, content);
    }
}
