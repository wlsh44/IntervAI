package wlsh.project.intervai.report.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.interview.application.InterviewFinder;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.profile.application.ProfileFinder;
import wlsh.project.intervai.profile.domain.Profile;
import wlsh.project.intervai.session.application.InterviewSessionFinder;
import wlsh.project.intervai.session.domain.InterviewSession;

@Component
@RequiredArgsConstructor
public class InterviewReportContextFinder {

    private final InterviewFinder interviewFinder;
    private final InterviewSessionFinder interviewSessionFinder;
    private final ProfileFinder profileFinder;

    public InterviewReportContext find(Long interviewId) {
        Interview interview = interviewFinder.find(interviewId);
        InterviewSession session = interviewSessionFinder.findByInterviewId(interviewId);
        Profile profile = profileFinder.findByUserId(interview.getUserId());

        return new InterviewReportContext(interview, session, profile.getJobCategory().name());
    }

    public record InterviewReportContext(
            Interview interview,
            InterviewSession session,
            String jobCategory
    ) {
    }
}
