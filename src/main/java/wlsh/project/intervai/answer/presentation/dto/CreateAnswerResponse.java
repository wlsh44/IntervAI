package wlsh.project.intervai.answer.presentation.dto;

import wlsh.project.intervai.answer.domain.AnswerResult;

public record CreateAnswerResponse(
        Long answerId,
        String feedback,
        String followUpQuestion
) {
    public static CreateAnswerResponse of(AnswerResult result) {
        return new CreateAnswerResponse(
                result.answerId(),
                result.feedback(),
                result.followUpQuestion()
        );
    }
}