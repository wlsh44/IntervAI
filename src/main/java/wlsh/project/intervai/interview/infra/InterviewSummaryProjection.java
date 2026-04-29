package wlsh.project.intervai.interview.infra;

import java.time.LocalDateTime;
import wlsh.project.intervai.interview.domain.Difficulty;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.session.domain.SessionStatus;

public interface InterviewSummaryProjection {
    Long getId();
    InterviewType getInterviewType();
    Difficulty getDifficulty();
    int getQuestionCount();
    LocalDateTime getCreatedAt();
    SessionStatus getSessionStatus();
}
