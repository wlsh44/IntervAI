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
            List<String> keywords,
            List<FollowUpQuestionResponse> followUps
    ) {
        public static QuestionReportResponse from(ReportQuestion q) {
            List<FollowUpQuestionResponse> followUps = q.followUps() == null ? List.of() :
                    q.followUps().stream().map(FollowUpQuestionResponse::from).toList();
            return new QuestionReportResponse(
                    q.questionId(),
                    q.questionIndex(),
                    q.questionContent(),
                    q.answerContent(),
                    q.feedbackContent(),
                    q.score(),
                    q.keywords(),
                    followUps
            );
        }
    }

    public record FollowUpQuestionResponse(
            Long questionId,
            String questionContent,
            String answerContent,
            String feedbackContent
    ) {
        public static FollowUpQuestionResponse from(ReportQuestion.FollowUpQuestion f) {
            return new FollowUpQuestionResponse(
                    f.questionId(),
                    f.questionContent(),
                    f.answerContent(),
                    f.feedbackContent()
            );
        }
    }
}
