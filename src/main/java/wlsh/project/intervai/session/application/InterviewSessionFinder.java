package wlsh.project.intervai.session.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.session.domain.InterviewSession;
import wlsh.project.intervai.session.infra.InterviewSessionEntity;
import wlsh.project.intervai.session.infra.InterviewSessionRepository;

@Component
@RequiredArgsConstructor
public class InterviewSessionFinder {

    private final InterviewSessionRepository interviewSessionRepository;

    public InterviewSessionEntity getEntity(Long sessionId) {
        return interviewSessionRepository.findByIdAndStatus(sessionId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));
    }

    public InterviewSession find(Long sessionId) {
        return getEntity(sessionId).toDomain();
    }

    public InterviewSessionEntity getEntityByInterviewId(Long interviewId) {
        return interviewSessionRepository.findByInterviewIdAndStatus(interviewId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));
    }

    public InterviewSession findByInterviewId(Long interviewId) {
        return getEntityByInterviewId(interviewId).toDomain();
    }
}
