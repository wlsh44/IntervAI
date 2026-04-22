package wlsh.project.intervai.report.presentation.dto;

import wlsh.project.intervai.interview.domain.Difficulty;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.report.domain.InterviewReport;
import wlsh.project.intervai.report.domain.ReportQuestion;

import java.time.LocalDateTime;
import java.util.List;

public record InterviewReportResponse(
        Long interviewId,
        InterviewType interviewType,
        String jobCategory,
        Difficulty difficulty,
        int questionCount,
        LocalDateTime completedAt,
        int totalScore,
        String overallComment,
        List<QuestionReportResponse> questions
) {

    public static InterviewReportResponse from(InterviewReport report) {
        return new InterviewReportResponse(
                report.interviewId(),
                report.interviewType(),
                report.jobCategory(),
                report.difficulty(),
                report.questionCount(),
                report.completedAt(),
                report.totalScore(),
                report.overallComment(),
                report.questions().stream()
                        .map(QuestionReportResponse::from)
                        .toList()
        );
    }

    public record QuestionReportResponse(
            Long questionId,
            int questionIndex,
            String questionContent,
            String answerContent,
            String feedbackContent,
            Integer score,
            List<String> keywords
    ) {
        public static QuestionReportResponse from(ReportQuestion q) {
            return new QuestionReportResponse(
                    q.questionId(),
                    q.questionIndex(),
                    q.questionContent(),
                    q.answerContent(),
                    q.feedbackContent(),
                    q.score(),
                    q.keywords()
            );
        }
    }
}
