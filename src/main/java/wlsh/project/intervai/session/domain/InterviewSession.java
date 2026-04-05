package wlsh.project.intervai.session.domain;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class InterviewSession {

    private final Long id;
    private final Long interviewId;
    private final Long userId;
    private final InterviewSessionStatus sessionStatus;
    private final int currentQuestionIndex;
    private final LocalDateTime completedAt;

    private InterviewSession(Long id, Long interviewId, Long userId, InterviewSessionStatus sessionStatus,
                             int currentQuestionIndex, LocalDateTime completedAt) {
        this.id = id;
        this.interviewId = interviewId;
        this.userId = userId;
        this.sessionStatus = sessionStatus;
        this.currentQuestionIndex = currentQuestionIndex;
        this.completedAt = completedAt;
    }

    public static InterviewSession create(Long interviewId, Long userId) {
        return new InterviewSession(null, interviewId, userId, InterviewSessionStatus.IN_PROGRESS, -1, null);
    }

    public static InterviewSession of(Long id, Long interviewId, Long userId, InterviewSessionStatus sessionStatus,
                                      int currentQuestionIndex, LocalDateTime completedAt) {
        return new InterviewSession(id, interviewId, userId, sessionStatus, currentQuestionIndex, completedAt);
    }
}
