package wlsh.project.intervai.interview.domain;

import java.util.List;

public record CreateInterviewCommand(
        InterviewType interviewType,
        Difficulty difficulty,
        int questionCount,
        InterviewerPersonality interviewerPersonality,
        List<CsSubject> csSubjects,
        List<String> portfolioLinks
) {
}
