package wlsh.project.intervai.interview.domain;

import java.time.LocalDateTime;
import wlsh.project.intervai.interview.infra.InterviewEntity;
import wlsh.project.intervai.session.domain.SessionStatus;

public record InterviewSummary(Long id, InterviewType interviewType, Difficulty difficulty, int questionCount,
                               SessionStatus sessionStatus, Integer totalScore, LocalDateTime createdAt) {

    public static InterviewSummary of(InterviewEntity entity, SessionStatus sessionStatus, Integer totalScore) {
        return new InterviewSummary(
                entity.getId(),
                entity.getInterviewType(),
                entity.getDifficulty(),
                entity.getQuestionCount(),
                sessionStatus,
                totalScore,
                entity.getCreatedAt()
        );
    }

    public static InterviewSummary of(Long id, InterviewType interviewType, Difficulty difficulty,
                                      int questionCount, SessionStatus sessionStatus, Integer totalScore,
                                      LocalDateTime createdAt) {
        return new InterviewSummary(id, interviewType, difficulty, questionCount, sessionStatus, totalScore, createdAt);
    }
}
