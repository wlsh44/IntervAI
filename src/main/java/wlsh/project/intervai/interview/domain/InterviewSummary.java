package wlsh.project.intervai.interview.domain;

import java.time.LocalDateTime;
import wlsh.project.intervai.interview.infra.InterviewEntity;
import wlsh.project.intervai.session.domain.SessionStatus;

public class InterviewSummary {

    private final Long id;
    private final InterviewType interviewType;
    private final Difficulty difficulty;
    private final int questionCount;
    private final SessionStatus sessionStatus;
    private final LocalDateTime createdAt;

    private InterviewSummary(Long id, InterviewType interviewType, Difficulty difficulty,
                             int questionCount, SessionStatus sessionStatus, LocalDateTime createdAt) {
        this.id = id;
        this.interviewType = interviewType;
        this.difficulty = difficulty;
        this.questionCount = questionCount;
        this.sessionStatus = sessionStatus;
        this.createdAt = createdAt;
    }

    public static InterviewSummary of(InterviewEntity entity, SessionStatus sessionStatus) {
        return new InterviewSummary(
                entity.getId(),
                entity.getInterviewType(),
                entity.getDifficulty(),
                entity.getQuestionCount(),
                sessionStatus,
                entity.getCreatedAt()
        );
    }

    public static InterviewSummary of(Long id, InterviewType interviewType, Difficulty difficulty,
                                      int questionCount, SessionStatus sessionStatus, LocalDateTime createdAt) {
        return new InterviewSummary(id, interviewType, difficulty, questionCount, sessionStatus, createdAt);
    }

    public Long getId() {
        return id;
    }

    public InterviewType getInterviewType() {
        return interviewType;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public SessionStatus getSessionStatus() {
        return sessionStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
