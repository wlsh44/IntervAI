package wlsh.project.intervai.session.presentation.dto;

import wlsh.project.intervai.session.application.dto.SessionHistoryResult;
import wlsh.project.intervai.session.domain.SessionStatus;

import java.util.List;

public record SessionHistoryResponse(
        Long sessionId,
        SessionStatus sessionStatus,
        int questionCount,
        List<SessionHistoryEntryResponse> entries
) {
    public static SessionHistoryResponse from(SessionHistoryResult result) {
        return new SessionHistoryResponse(
                result.sessionId(),
                result.sessionStatus(),
                result.questionCount(),
                result.entries().stream()
                        .map(SessionHistoryEntryResponse::from)
                        .toList()
        );
    }
}
