package wlsh.project.intervai.interview.domain;

import java.util.List;

public record CreateInterviewCommand(
        InterviewType interviewType,
        Difficulty difficulty,
        int questionCount,
        InterviewerTone interviewerTone,
        List<CsSubject> csSubjects,
        List<String> portfolioLinks
) {
}
