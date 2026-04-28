package wlsh.project.intervai.report.application;

import java.time.LocalDateTime;
import java.util.List;
import wlsh.project.intervai.interview.domain.Difficulty;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.report.application.dto.ReportGenerationResultDto.QuestionKeywords;

public record StoredInterviewReport(
        Long id,
        Long interviewId,
        InterviewType interviewType,
        String jobCategory,
        Difficulty difficulty,
        int questionCount,
        LocalDateTime completedAt,
        int totalScore,
        String overallComment,
        List<QuestionKeywords> keywords
) {
}
