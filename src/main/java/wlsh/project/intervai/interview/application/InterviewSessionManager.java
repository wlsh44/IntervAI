package wlsh.project.intervai.interview.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import wlsh.project.intervai.interview.domain.CsSubject;
import wlsh.project.intervai.interview.domain.InterviewSession;
import wlsh.project.intervai.interview.infra.InterviewCsSubjectEntity;
import wlsh.project.intervai.interview.infra.InterviewCsSubjectRepository;
import wlsh.project.intervai.interview.infra.InterviewPortfolioLinkEntity;
import wlsh.project.intervai.interview.infra.InterviewPortfolioLinkRepository;
import wlsh.project.intervai.interview.infra.InterviewSessionEntity;
import wlsh.project.intervai.interview.infra.InterviewSessionRepository;

@Component
@RequiredArgsConstructor
public class InterviewSessionManager {

    private final InterviewSessionRepository interviewSessionRepository;
    private final InterviewCsSubjectRepository interviewCsSubjectRepository;
    private final InterviewPortfolioLinkRepository interviewPortfolioLinkRepository;

    @Transactional
    public InterviewSession create(InterviewSession session) {
        InterviewSessionEntity sessionEntity = interviewSessionRepository.save(
                InterviewSessionEntity.from(session));

        saveCsSubjects(sessionEntity.getId(), session.getCsSubjects());
        savePortfolioLinks(sessionEntity.getId(), session.getPortfolioLinks());

        return sessionEntity.toDomain(session.getCsSubjects(), session.getPortfolioLinks());
    }

    private void saveCsSubjects(Long sessionId, List<CsSubject> csSubjects) {
        if (csSubjects == null || csSubjects.isEmpty()) {
            return;
        }
        List<InterviewCsSubjectEntity> entities = csSubjects.stream()
                .flatMap(subject -> subject.getTopics().stream()
                        .map(topic -> InterviewCsSubjectEntity.of(sessionId, subject.getCategory(), topic)))
                .toList();
        interviewCsSubjectRepository.saveAll(entities);
    }

    private void savePortfolioLinks(Long sessionId, List<String> portfolioLinks) {
        if (portfolioLinks == null || portfolioLinks.isEmpty()) {
            return;
        }
        List<InterviewPortfolioLinkEntity> entities = portfolioLinks.stream()
                .map(url -> InterviewPortfolioLinkEntity.of(sessionId, url))
                .toList();
        interviewPortfolioLinkRepository.saveAll(entities);
    }
}
