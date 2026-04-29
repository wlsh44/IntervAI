package wlsh.project.intervai.interview.application;

import java.util.LinkedHashMap;
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
import wlsh.project.intervai.interview.domain.CsCategory;
import wlsh.project.intervai.interview.domain.CsSubject;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.interview.domain.InterviewSummary;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.interview.infra.InterviewCsSubjectEntity;
import wlsh.project.intervai.interview.infra.InterviewCsSubjectRepository;
import wlsh.project.intervai.interview.infra.InterviewEntity;
import wlsh.project.intervai.interview.infra.InterviewPortfolioLinkEntity;
import wlsh.project.intervai.interview.infra.InterviewPortfolioLinkRepository;
import wlsh.project.intervai.interview.infra.InterviewRepository;
import wlsh.project.intervai.session.domain.SessionStatus;

@Component
@RequiredArgsConstructor
public class InterviewFinder {

    private final InterviewRepository interviewRepository;
    private final InterviewCsSubjectRepository interviewCsSubjectRepository;
    private final InterviewPortfolioLinkRepository interviewPortfolioLinkRepository;

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
    public Page<InterviewSummary> findSummaries(Long userId, InterviewType interviewType, SessionStatus sessionStatus, Pageable pageable) {
        return interviewRepository.findSummaries(userId, interviewType, sessionStatus, pageable)
                .map(InterviewSummary::of);
    }
}
