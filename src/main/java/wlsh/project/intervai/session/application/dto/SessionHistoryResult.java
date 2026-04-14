package wlsh.project.intervai.session.application.dto;

import wlsh.project.intervai.session.domain.SessionStatus;

import java.util.List;

public record SessionHistoryResult(
        Long sessionId,
        SessionStatus sessionStatus,
        int questionCount,
        List<SessionHistoryEntryResult> entries
) {
}
