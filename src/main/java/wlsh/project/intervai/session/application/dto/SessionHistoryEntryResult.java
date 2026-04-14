package wlsh.project.intervai.session.application.dto;

import wlsh.project.intervai.question.domain.QuestionType;

public record SessionHistoryEntryResult(
        Long questionId,
        String question,
        QuestionType questionType,
        String answer,
        String feedback
) {
}
