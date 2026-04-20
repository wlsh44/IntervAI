package wlsh.project.intervai.session.domain;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class InterviewSession {

    private final Long id;
    private final Long interviewId;
    private final Long userId;
    private final SessionStatus sessionStatus;
    private final int currentMainQuestionIdx;
    private final int followUpCount;
    private final LocalDateTime completedAt;

    private InterviewSession(Long id, Long interviewId, Long userId, SessionStatus sessionStatus,
                             int currentMainQuestionIdx, int followUpCount, LocalDateTime completedAt) {
        this.id = id;
        this.interviewId = interviewId;
        this.userId = userId;
        this.sessionStatus = sessionStatus;
        this.currentMainQuestionIdx = currentMainQuestionIdx;
        this.followUpCount = followUpCount;
        this.completedAt = completedAt;
    }

    public static InterviewSession create(Long interviewId, Long userId) {
        return new InterviewSession(null, interviewId, userId, SessionStatus.IN_PROGRESS, 0, 0, null);
    }

    public static InterviewSession of(Long id, Long interviewId, Long userId, SessionStatus sessionStatus,
                                      int currentMainQuestionIdx, int followUpCount, LocalDateTime completedAt) {
        return new InterviewSession(id, interviewId, userId, sessionStatus, currentMainQuestionIdx, followUpCount, completedAt);
    }

    public boolean isCompleted() {
        return this.sessionStatus == SessionStatus.COMPLETED;
    }
}
