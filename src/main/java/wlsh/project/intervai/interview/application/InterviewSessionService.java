package wlsh.project.intervai.interview.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wlsh.project.intervai.interview.domain.CreateInterviewSessionCommand;
import wlsh.project.intervai.interview.domain.InterviewSession;

@Service
@RequiredArgsConstructor
public class InterviewSessionService {

    private final InterviewSessionValidator interviewSessionValidator;
    private final InterviewSessionManager interviewSessionManager;

    public InterviewSession create(Long userId, CreateInterviewSessionCommand command) {
        interviewSessionValidator.validate(command);
        InterviewSession session = InterviewSession.create(userId, command);
        return interviewSessionManager.create(session);
    }
}
