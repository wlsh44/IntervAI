package wlsh.project.intervai.session.presentation.dto;

import wlsh.project.intervai.session.domain.InterviewSession;
import wlsh.project.intervai.session.domain.InterviewSessionStatus;

public record CreateInterviewSessionResponse(
        Long id,
        Long interviewId,
        InterviewSessionStatus sessionStatus,
        int currentQuestionCount
) {
    public static CreateInterviewSessionResponse of(InterviewSession session) {
        return new CreateInterviewSessionResponse(
                session.getId(),
                session.getInterviewId(),
                session.getSessionStatus(),
                session.getCurrentQuestionCount()
        );
    }
}
