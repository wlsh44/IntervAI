package wlsh.project.intervai.interview.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.interview.domain.CsCategory;
import wlsh.project.intervai.interview.domain.CsSubject;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.interview.domain.InterviewListQuery;
import wlsh.project.intervai.interview.domain.InterviewSummary;
import wlsh.project.intervai.interview.infra.InterviewCsSubjectEntity;
import wlsh.project.intervai.interview.infra.InterviewCsSubjectRepository;
import wlsh.project.intervai.interview.infra.InterviewEntity;
import wlsh.project.intervai.interview.infra.InterviewPortfolioLinkEntity;
import wlsh.project.intervai.interview.infra.InterviewPortfolioLinkRepository;
import wlsh.project.intervai.interview.infra.InterviewRepository;
import wlsh.project.intervai.interview.infra.InterviewSpecification;
import wlsh.project.intervai.report.infra.InterviewReportEntity;
import wlsh.project.intervai.report.infra.InterviewReportRepository;
import wlsh.project.intervai.session.domain.SessionStatus;
import wlsh.project.intervai.session.infra.InterviewSessionEntity;
import wlsh.project.intervai.session.infra.InterviewSessionRepository;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InterviewFinder {

    private final InterviewRepository interviewRepository;
    private final InterviewCsSubjectRepository interviewCsSubjectRepository;
    private final InterviewPortfolioLinkRepository interviewPortfolioLinkRepository;
    private final InterviewSessionRepository interviewSessionRepository;
    private final InterviewReportRepository interviewReportRepository;

    public Interview find(Long interviewId) {
        InterviewEntity interview = interviewRepository.findByIdAndStatus(interviewId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.INTERVIEW_NOT_FOUND));
        return interview.toDomain(findCsSubjects(interviewId), findPortfolioLinks(interviewId), List.of());
    }

    private List<CsSubject> findCsSubjects(Long interviewId) {
        Map<CsCategory, List<String>> topicsByCategory = interviewCsSubjectRepository
                .findByInterviewIdAndStatusOrderByIdAsc(interviewId, EntityStatus.ACTIVE)
                .stream()
                .collect(Collectors.groupingBy(
                        InterviewCsSubjectEntity::getCategory,
                        LinkedHashMap::new,
                        Collectors.mapping(InterviewCsSubjectEntity::getTopic, Collectors.toList())
                ));

        return topicsByCategory.entrySet().stream()
                .map(entry -> CsSubject.of(entry.getKey(), entry.getValue()))
                .toList();
    }

    private List<String> findPortfolioLinks(Long interviewId) {
        return interviewPortfolioLinkRepository.findByInterviewIdAndStatusOrderByIdAsc(interviewId, EntityStatus.ACTIVE)
                .stream()
                .map(InterviewPortfolioLinkEntity::getUrl)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<InterviewSummary> findSummaries(Long userId, InterviewListQuery query, Pageable pageable) {
        Page<InterviewEntity> interviewPage = interviewRepository
                .findAll(InterviewSpecification.of(userId, query), pageable);

        List<Long> interviewIds = interviewPage.getContent().stream()
                .map(InterviewEntity::getId)
                .toList();

        Map<Long, SessionStatus> sessionStatusMap = interviewSessionRepository
                .findByInterviewIdInAndStatus(interviewIds, EntityStatus.ACTIVE)
                .stream()
                .collect(Collectors.toMap(
                        InterviewSessionEntity::getInterviewId,
                        InterviewSessionEntity::getSessionStatus,
                        (existing, replacement) -> replacement));

        Map<Long, Integer> totalScoreMap = interviewReportRepository
                .findByInterviewIdInAndStatus(interviewIds, EntityStatus.ACTIVE)
                .stream()
                .collect(Collectors.toMap(
                        InterviewReportEntity::getInterviewId,
                        InterviewReportEntity::getTotalScore,
                        (existing, replacement) -> replacement));

        return interviewPage.map(entity -> {
            SessionStatus sessionStatus = sessionStatusMap.get(entity.getId());
            Integer totalScore = SessionStatus.COMPLETED.equals(sessionStatus)
                    ? totalScoreMap.get(entity.getId())
                    : null;
            return InterviewSummary.of(entity, sessionStatus, totalScore);
        });
    }
}
