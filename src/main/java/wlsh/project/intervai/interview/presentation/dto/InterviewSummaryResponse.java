package wlsh.project.intervai.interview.presentation.dto;

import java.time.LocalDateTime;
import wlsh.project.intervai.interview.domain.Difficulty;
import wlsh.project.intervai.interview.domain.InterviewSummary;
import wlsh.project.intervai.interview.domain.InterviewType;

public record InterviewSummaryResponse(
        Long id,
        InterviewType interviewType,
        Difficulty difficulty,
        int questionCount,
        SessionStatus sessionStatus,
        LocalDateTime createdAt
) {

    public static InterviewSummaryResponse of(InterviewSummary summary) {
        return new InterviewSummaryResponse(
                summary.getId(),
                summary.getInterviewType(),
                summary.getDifficulty(),
                summary.getQuestionCount(),
                summary.getSessionStatus(),
                summary.getCreatedAt()
        );
    }
}
