package wlsh.project.intervai.report.domain;

import java.time.LocalDateTime;
import java.util.List;

import wlsh.project.intervai.interview.domain.Difficulty;
import wlsh.project.intervai.interview.domain.InterviewType;

public record InterviewReport(
        Long id,
        Long interviewId,
        InterviewType interviewType,
        String jobCategory,
        Difficulty difficulty,
        int questionCount,
        LocalDateTime completedAt,
        int totalScore,
        String overallComment,
        List<ReportQuestion> questions
) {

    public static InterviewReport create(
            Long interviewId,
            InterviewType interviewType,
            String jobCategory,
            Difficulty difficulty,
            int questionCount,
            LocalDateTime completedAt,
            int totalScore,
            String overallComment,
            List<ReportQuestion> questions
    ) {
        return new InterviewReport(
                null, interviewId, interviewType, jobCategory, difficulty,
                questionCount, completedAt, totalScore, overallComment, questions
        );
    }
}
