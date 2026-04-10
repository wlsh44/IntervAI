package wlsh.project.intervai.interview.application;

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
import wlsh.project.intervai.interview.infra.InterviewEntity;
import wlsh.project.intervai.interview.infra.InterviewRepository;
import wlsh.project.intervai.session.domain.SessionStatus;
import wlsh.project.intervai.session.infra.InterviewSessionEntity;
import wlsh.project.intervai.session.infra.InterviewSessionRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InterviewFinder {

    private final InterviewRepository interviewRepository;
    private final InterviewSessionRepository interviewSessionRepository;

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
                .toList();

        Map<Long, InterviewSessionEntity> sessionMap = interviewSessionRepository
                .findByInterviewIdInAndStatus(interviewIds, EntityStatus.ACTIVE)
                .stream()
                .collect(Collectors.toMap(InterviewSessionEntity::getInterviewId, s -> s));

        return interviewPage.map(entity -> {
            InterviewSessionEntity session = sessionMap.get(entity.getId());
            SessionStatus sessionStatus = session.getSessionStatus();
            return InterviewSummary.of(entity, sessionStatus);
        });
    }
}
