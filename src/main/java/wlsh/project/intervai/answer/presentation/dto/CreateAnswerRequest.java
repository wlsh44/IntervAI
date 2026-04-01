package wlsh.project.intervai.answer.presentation.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateAnswerRequest(
        @NotNull Long questionId,
        @NotEmpty String content
) {
}
