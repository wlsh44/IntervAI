package wlsh.project.intervai.session.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.session.domain.InterviewSession;
import wlsh.project.intervai.session.infra.InterviewSessionEntity;
import wlsh.project.intervai.session.infra.InterviewSessionRepository;

@Component
@RequiredArgsConstructor
public class InterviewSessionManager {

    private final InterviewSessionRepository interviewSessionRepository;

    @Transactional
    public InterviewSession create(Long interviewId, Long userId) {
        InterviewSession session = InterviewSession.create(interviewId, userId);
        InterviewSessionEntity entity = interviewSessionRepository.save(InterviewSessionEntity.from(session));
        return entity.toDomain();
    }

    @Transactional
    public void advanceToNext(Long sessionId) {
        InterviewSessionEntity entity = interviewSessionRepository.findByIdAndStatus(sessionId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));
        entity.advanceToNext();
    }

    @Transactional
    public void addFollowUpCount(Long sessionId) {
        InterviewSessionEntity entity = interviewSessionRepository.findByIdAndStatus(sessionId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));
        entity.addFollowUpCount();
    }

    @Transactional
    public void complete(Long interviewId) {
        InterviewSessionEntity entity = interviewSessionRepository.findByInterviewIdAndStatus(interviewId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));
        entity.complete();
    }
}
