package wlsh.project.intervai.session.presentation.dto;

import wlsh.project.intervai.question.domain.QuestionType;
import wlsh.project.intervai.session.application.dto.SessionHistoryEntryResult;

public record SessionHistoryEntryResponse(
        Long questionId,
        String question,
        QuestionType questionType,
        String answer,
        String feedback
) {
    public static SessionHistoryEntryResponse from(SessionHistoryEntryResult result) {
        return new SessionHistoryEntryResponse(
                result.questionId(),
                result.question(),
                result.questionType(),
                result.answer(),
                result.feedback()
        );
    }
}
