package wlsh.project.intervai.question.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.session.domain.InterviewSession;
import wlsh.project.intervai.session.domain.InterviewSessionStatus;

@Component
@RequiredArgsConstructor
public class QuestionValidator {

    private final QuestionFinder questionFinder;

    public void validate(InterviewSession session, Interview interview) {
        validateSessionInProgress(session);
        validateQuestionCountNotExceeded(session, interview);
    }

    private void validateSessionInProgress(InterviewSession session) {
        if (session.getSessionStatus() != InterviewSessionStatus.IN_PROGRESS) {
            throw new CustomException(ErrorCode.SESSION_ALREADY_COMPLETED);
        }
    }

    private void validateQuestionCountNotExceeded(InterviewSession session, Interview interview) {
        int currentCount = questionFinder.countBySessionId(session.getId());
        if (currentCount >= interview.getQuestionCount()) {
            throw new CustomException(ErrorCode.QUESTION_COUNT_EXCEEDED);
        }
    }
}
