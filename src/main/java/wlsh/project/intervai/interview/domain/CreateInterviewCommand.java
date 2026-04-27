package wlsh.project.intervai.interview.domain;

import java.util.List;
import wlsh.project.intervai.common.domain.JobCategory;

public record CreateInterviewCommand(
        JobCategory jobCategory,
        InterviewType interviewType,
        Difficulty difficulty,
        int questionCount,
        InterviewerTone interviewerTone,
        List<CsSubject> csSubjects,
        List<String> portfolioLinks,
        List<String> techStacks
) {
}
