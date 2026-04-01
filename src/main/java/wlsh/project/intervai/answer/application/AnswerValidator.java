package wlsh.project.intervai.answer.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.answer.infra.AnswerRepository;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.session.domain.InterviewSession;
import wlsh.project.intervai.session.domain.InterviewSessionStatus;

@Component
@RequiredArgsConstructor
public class AnswerValidator {

    private final AnswerRepository answerRepository;

    public void validate(InterviewSession session, Long questionId) {
        validateSessionInProgress(session);
        validateNotAlreadyAnswered(questionId);
    }

    private void validateSessionInProgress(InterviewSession session) {
        if (session.getSessionStatus() != InterviewSessionStatus.IN_PROGRESS) {
            throw new CustomException(ErrorCode.SESSION_ALREADY_COMPLETED);
        }
    }

    private void validateNotAlreadyAnswered(Long questionId) {
        if (answerRepository.existsByQuestionIdAndStatus(questionId, EntityStatus.ACTIVE)) {
            throw new CustomException(ErrorCode.ANSWER_ALREADY_EXISTS);
        }
    }
}
