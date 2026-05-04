package wlsh.project.intervai.interview.presentation.dto;

import java.time.LocalDateTime;
import wlsh.project.intervai.interview.domain.Difficulty;
import wlsh.project.intervai.interview.domain.InterviewSummary;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.session.domain.SessionStatus;

public record InterviewSummaryResponse(
        Long id,
        InterviewType interviewType,
        Difficulty difficulty,
        int questionCount,
        SessionStatus sessionStatus,
        Integer totalScore,
        LocalDateTime createdAt
) {

    public static InterviewSummaryResponse of(InterviewSummary summary) {
        return new InterviewSummaryResponse(
                summary.id(),
                summary.interviewType(),
                summary.difficulty(),
                summary.questionCount(),
                summary.sessionStatus(),
                summary.totalScore(),
                summary.createdAt()
        );
    }
}
