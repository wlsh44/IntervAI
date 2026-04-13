package wlsh.project.intervai.interview.application;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import wlsh.project.intervai.interview.domain.CreateInterviewCommand;
import wlsh.project.intervai.interview.domain.CsSubject;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.interview.infra.InterviewCsSubjectEntity;
import wlsh.project.intervai.interview.infra.InterviewCsSubjectRepository;
import wlsh.project.intervai.interview.infra.InterviewEntity;
import wlsh.project.intervai.interview.infra.InterviewPortfolioLinkEntity;
import wlsh.project.intervai.interview.infra.InterviewPortfolioLinkRepository;
import wlsh.project.intervai.interview.infra.InterviewRepository;

@Component
@RequiredArgsConstructor
public class InterviewManager {

    private final InterviewRepository interviewRepository;
    private final InterviewCsSubjectRepository interviewCsSubjectRepository;
    private final InterviewPortfolioLinkRepository interviewPortfolioLinkRepository;

    @Transactional
    public Interview create(Long userId, CreateInterviewCommand command) {
        Interview interview = Interview.create(userId, command);
        InterviewEntity interviewEntity = interviewRepository.save(InterviewEntity.from(interview));

        saveCsSubjects(interviewEntity.getId(), interview.getCsSubjects());
        savePortfolioLinks(interviewEntity.getId(), interview.getPortfolioLinks());
        saveTechStacks(interviewEntity.getId(), interview.getTechStacks());

        return interviewEntity.toDomain(interview.getCsSubjects(), interview.getPortfolioLinks(), interview.getTechStacks());
    }

    private void saveTechStacks(Long interviewId, List<String> techStacks) {
        if (CollectionUtils.isEmpty(techStacks)) {
            return;
        }
        //TODO
    }

    private void saveCsSubjects(Long interviewId, List<CsSubject> csSubjects) {
        if (CollectionUtils.isEmpty(csSubjects)) {
            return;
        }
        List<InterviewCsSubjectEntity> entities = csSubjects.stream()
                .flatMap(subject -> subject.getTopics().stream()
                        .map(topic -> InterviewCsSubjectEntity.of(interviewId, subject.getCategory(), topic)))
                .toList();
        interviewCsSubjectRepository.saveAll(entities);
    }

    private void savePortfolioLinks(Long interviewId, List<String> portfolioLinks) {
        if (CollectionUtils.isEmpty(portfolioLinks)) {
            return;
        }
        List<InterviewPortfolioLinkEntity> entities = portfolioLinks.stream()
                .map(url -> InterviewPortfolioLinkEntity.of(interviewId, url))
                .toList();
        interviewPortfolioLinkRepository.saveAll(entities);
    }
}
