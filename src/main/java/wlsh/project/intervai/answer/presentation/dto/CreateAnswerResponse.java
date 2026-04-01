package wlsh.project.intervai.answer.presentation.dto;

import wlsh.project.intervai.answer.domain.Answer;

public record CreateAnswerResponse(
        Long answerId
) {
    public static CreateAnswerResponse create(Answer answer) {
        return new CreateAnswerResponse(answer.getId());
    }
}
