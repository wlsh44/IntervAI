package wlsh.project.intervai.session.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.interview.infra.InterviewEntity;
import wlsh.project.intervai.interview.infra.InterviewRepository;
import wlsh.project.intervai.session.infra.InterviewSessionEntity;
import wlsh.project.intervai.session.infra.InterviewSessionRepository;

@Component
@RequiredArgsConstructor
public class InterviewSessionValidator {

    private final InterviewRepository interviewRepository;
    private final InterviewSessionRepository interviewSessionRepository;

    public void validateInterviewSession(Long interviewId, Long userId) {
        validateInterviewOwner(interviewId, userId);
        validateSessionInProgress(interviewId);
    }

    public void validateInterviewOwner(Long interviewId, Long userId) {
        InterviewEntity interviewEntity = interviewRepository.findByIdAndStatus(interviewId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.INTERVIEW_NOT_FOUND));
        if (!interviewEntity.isOwner(userId)) {
            throw new CustomException(ErrorCode.INTERVIEW_ACCESS_DENIED);
        }
    }

    private void validateSessionInProgress(Long interviewId) {
        InterviewSessionEntity entity = interviewSessionRepository.findByInterviewIdAndStatus(interviewId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));
        if (!entity.isInProgress()) {
            throw new CustomException(ErrorCode.SESSION_ALREADY_COMPLETED);
        }
    }
}
