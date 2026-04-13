package wlsh.project.intervai.session.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wlsh.project.intervai.session.domain.InterviewSession;

@Service
@RequiredArgsConstructor
public class InterviewSessionService {

    private final InterviewSessionValidator interviewSessionValidator;
    private final InterviewSessionManager interviewSessionManager;

    public InterviewSession create(Long userId, Long interviewId) {
        interviewSessionValidator.validateInterviewOwner(interviewId, userId);
        return interviewSessionManager.create(interviewId, userId);
    }

    public void finish(Long userId, Long interviewId) {
        interviewSessionValidator.validateInterviewSession(interviewId, userId);
        interviewSessionManager.complete(interviewId);
    }
}
