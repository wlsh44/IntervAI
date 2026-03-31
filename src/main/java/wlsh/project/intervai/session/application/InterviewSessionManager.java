package wlsh.project.intervai.session.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.question.application.QuestionFinder;
import wlsh.project.intervai.session.domain.InterviewSession;
import wlsh.project.intervai.session.infra.InterviewSessionEntity;
import wlsh.project.intervai.session.infra.InterviewSessionRepository;

@Component
@RequiredArgsConstructor
public class InterviewSessionManager {

    private final InterviewSessionRepository interviewSessionRepository;
    private final QuestionFinder questionFinder;

    public InterviewSession create(Long interviewId, Long userId) {
        InterviewSession session = InterviewSession.create(interviewId, userId);
        InterviewSessionEntity entity = interviewSessionRepository.save(InterviewSessionEntity.from(session));
        int questionCount = questionFinder.countBySessionId(entity.getId());
        return entity.toDomain(questionCount);
    }
}
