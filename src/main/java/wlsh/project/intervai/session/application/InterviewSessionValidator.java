package wlsh.project.intervai.session.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.interview.infra.InterviewEntity;
import wlsh.project.intervai.interview.infra.InterviewRepository;
import wlsh.project.intervai.session.infra.InterviewSessionEntity;

@Component
@RequiredArgsConstructor
public class InterviewSessionValidator {

    private final InterviewRepository interviewRepository;
    private final InterviewSessionFinder interviewSessionFinder;

    public void validateInterviewOwner(Long interviewId, Long userId) {
        InterviewEntity interviewEntity = interviewRepository.findByIdAndStatus(interviewId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.INTERVIEW_NOT_FOUND));
        if (!interviewEntity.isOwner(userId)) {
            throw new CustomException(ErrorCode.INTERVIEW_ACCESS_DENIED);
        }
    }

    public void validateSessionInProgress(Long interviewId) {
        InterviewSessionEntity entity = interviewSessionFinder.getEntityByInterviewId(interviewId);
        if (!entity.isInProgress()) {
            throw new CustomException(ErrorCode.SESSION_ALREADY_COMPLETED);
        }
    }
}
