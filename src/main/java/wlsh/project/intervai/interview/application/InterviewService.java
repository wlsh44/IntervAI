package wlsh.project.intervai.interview.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wlsh.project.intervai.interview.domain.CreateInterviewCommand;
import wlsh.project.intervai.interview.domain.Interview;


@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewValidator interviewValidator;
    private final InterviewManager interviewManager;

    public Interview create(Long userId, CreateInterviewCommand command) {
        interviewValidator.validate(command);
        return interviewManager.create(userId, command);
    }
}
