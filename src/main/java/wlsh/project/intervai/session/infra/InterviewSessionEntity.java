package wlsh.project.intervai.session.infra;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wlsh.project.intervai.common.entity.BaseEntity;
import wlsh.project.intervai.session.domain.InterviewSession;
import wlsh.project.intervai.session.domain.InterviewSessionStatus;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "interview_sessions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InterviewSessionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long interviewId;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterviewSessionStatus sessionStatus;

    @Column(nullable = false)
    private int currentQuestionIndex;

    private LocalDateTime completedAt;

    private InterviewSessionEntity(Long interviewId, Long userId, InterviewSessionStatus sessionStatus,
                                   int currentQuestionIndex, LocalDateTime completedAt) {
        this.interviewId = interviewId;
        this.userId = userId;
        this.sessionStatus = sessionStatus;
        this.currentQuestionIndex = currentQuestionIndex;
        this.completedAt = completedAt;
    }

    public static InterviewSessionEntity from(InterviewSession session) {
        return new InterviewSessionEntity(
                session.getInterviewId(), session.getUserId(), session.getSessionStatus(),
                session.getCurrentQuestionIndex(), session.getCompletedAt()
        );
    }

    public void advanceQuestionIndex() {
        this.currentQuestionIndex++;
    }

    public void complete() {
        this.sessionStatus = InterviewSessionStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public InterviewSession toDomain() {
        return InterviewSession.of(id, interviewId, userId, sessionStatus, currentQuestionIndex, completedAt);
    }

    public boolean isOwner(Long userId) {
        return this.userId.equals(userId);
    }

    public boolean isInProgress() {
        return this.sessionStatus.equals(InterviewSessionStatus.IN_PROGRESS);
    }
}
