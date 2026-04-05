package wlsh.project.intervai.interview.presentation.dto;

import wlsh.project.intervai.session.domain.InterviewSession;

public record CreateSessionResponse(
        Long sessionId
) {
    public static CreateSessionResponse of(InterviewSession session) {
        return new CreateSessionResponse(session.getId());
    }
}
