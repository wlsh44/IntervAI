package wlsh.project.intervai.answer.presentation.dto;

import wlsh.project.intervai.answer.domain.AnswerResult;

public record CreateAnswerResponse(
        String feedback,
        int score
) {
    public static CreateAnswerResponse of(AnswerResult result) {
        return new CreateAnswerResponse(result.feedback(), result.score());
    }
}
