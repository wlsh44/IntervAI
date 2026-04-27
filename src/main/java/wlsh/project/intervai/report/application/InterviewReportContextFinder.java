package wlsh.project.intervai.report.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.interview.application.InterviewFinder;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.session.application.InterviewSessionFinder;
import wlsh.project.intervai.session.domain.InterviewSession;

@Component
@RequiredArgsConstructor
public class InterviewReportContextFinder {

    private final InterviewFinder interviewFinder;
    private final InterviewSessionFinder interviewSessionFinder;

    public InterviewReportContext find(Long interviewId) {
        Interview interview = interviewFinder.find(interviewId);
        InterviewSession session = interviewSessionFinder.findByInterviewId(interviewId);

        return new InterviewReportContext(interview, session, interview.getJobCategory().name());
    }

    public record InterviewReportContext(
            Interview interview,
            InterviewSession session,
            String jobCategory
    ) {
    }
}
