package wlsh.project.intervai.interview.domain;

import java.time.LocalDateTime;
import wlsh.project.intervai.interview.infra.InterviewSummaryProjection;
import wlsh.project.intervai.session.domain.SessionStatus;

public record InterviewSummary(Long id, InterviewType interviewType, Difficulty difficulty, int questionCount,
                               SessionStatus sessionStatus, LocalDateTime createdAt) {

    public static InterviewSummary of(InterviewSummaryProjection projection) {
        return new InterviewSummary(
                projection.getId(),
                projection.getInterviewType(),
                projection.getDifficulty(),
                projection.getQuestionCount(),
                projection.getSessionStatus(),
                projection.getCreatedAt()
        );
    }

    public static InterviewSummary of(Long id, InterviewType interviewType, Difficulty difficulty,
                                      int questionCount, SessionStatus sessionStatus, LocalDateTime createdAt) {
        return new InterviewSummary(id, interviewType, difficulty, questionCount, sessionStatus, createdAt);
    }
}
