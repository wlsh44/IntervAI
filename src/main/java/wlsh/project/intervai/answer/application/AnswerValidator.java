package wlsh.project.intervai.answer.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.answer.infra.AnswerRepository;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.interview.infra.InterviewEntity;
import wlsh.project.intervai.interview.infra.InterviewRepository;
import wlsh.project.intervai.session.domain.InterviewSession;
import wlsh.project.intervai.session.domain.InterviewSessionStatus;
import wlsh.project.intervai.session.infra.InterviewSessionEntity;
import wlsh.project.intervai.session.infra.InterviewSessionRepository;

@Component
@RequiredArgsConstructor
public class AnswerValidator {

    private final AnswerRepository answerRepository;
    private final InterviewRepository interviewRepository;
    private final InterviewSessionRepository interviewSessionRepository;

    public void validate(Long userId, Long interviewId, Long questionId) {
        validateInterviewOwner(interviewId, userId);
        validateSessionInProgress(interviewId);
        validateNotAlreadyAnswered(questionId);
    }

    private void validateInterviewOwner(Long interviewId, Long userId) {
        InterviewEntity interview = interviewRepository.findByIdAndStatus(interviewId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.INTERVIEW_NOT_FOUND));
        if (!interview.isOwner(userId)) {
            throw new CustomException(ErrorCode.INTERVIEW_ACCESS_DENIED);
        }
    }

    private void validateSessionInProgress(Long interviewId) {
        InterviewSessionEntity session = interviewSessionRepository.findByInterviewIdAndStatus(interviewId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));
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
