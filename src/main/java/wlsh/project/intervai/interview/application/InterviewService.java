package wlsh.project.intervai.interview.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import wlsh.project.intervai.interview.domain.CreateInterviewCommand;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.interview.domain.InterviewSummary;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewValidator interviewValidator;
    private final InterviewManager interviewManager;
    private final InterviewFinder interviewFinder;

    public Interview create(Long userId, CreateInterviewCommand command) {
        interviewValidator.validate(command);
        return interviewManager.create(userId, command);
    }

    public Page<InterviewSummary> getList(Long userId, Pageable pageable) {
        return interviewFinder.findSummaries(userId, pageable);
    }
}
