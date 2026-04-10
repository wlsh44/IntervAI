package wlsh.project.intervai.interview.application;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.interview.domain.InterviewSummary;
import wlsh.project.intervai.interview.domain.SessionStatus;
import wlsh.project.intervai.interview.infra.InterviewEntity;
import wlsh.project.intervai.interview.infra.InterviewRepository;
import wlsh.project.intervai.session.application.InterviewSessionFinder;
import wlsh.project.intervai.session.domain.InterviewSessionStatus;
import wlsh.project.intervai.session.infra.InterviewSessionEntity;

@Component
@RequiredArgsConstructor
public class InterviewFinder {

    private final InterviewRepository interviewRepository;
    private final InterviewSessionFinder interviewSessionFinder;

    public Interview find(Long interviewId) {
        return interviewRepository.findByIdAndStatus(interviewId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.INTERVIEW_NOT_FOUND))
                .toDomain();
    }

    @Transactional(readOnly = true)
    public Page<InterviewSummary> findSummaries(Long userId, Pageable pageable) {
        Page<InterviewEntity> interviewPage = interviewRepository
                .findByUserIdAndStatusOrderByCreatedAtDesc(userId, EntityStatus.ACTIVE, pageable);

        List<Long> interviewIds = interviewPage.getContent().stream()
                .map(InterviewEntity::getId)
                .collect(Collectors.toList());

        Map<Long, InterviewSessionEntity> sessionMap = interviewSessionFinder
                .findByInterviewIds(interviewIds)
                .stream()
                .collect(Collectors.toMap(InterviewSessionEntity::getInterviewId, s -> s));

        return interviewPage.map(entity -> {
            InterviewSessionEntity session = sessionMap.get(entity.getId());
            SessionStatus sessionStatus = resolveSessionStatus(session);
            return InterviewSummary.of(entity, sessionStatus);
        });
    }

    private SessionStatus resolveSessionStatus(InterviewSessionEntity session) {
        if (session == null) {
            return SessionStatus.IN_PROGRESS;
        }
        return session.getSessionStatus() == InterviewSessionStatus.COMPLETED
                ? SessionStatus.COMPLETED
                : SessionStatus.IN_PROGRESS;
    }
}
