package wlsh.project.intervai.answer.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import wlsh.project.intervai.answer.domain.CreateAnswerCommand;

public record CreateAnswerRequest(
        @NotNull(message = "질문 ID는 필수입니다.")
        Long questionId,

        @NotBlank(message = "답변 내용은 필수입니다.")
        String content
) {
    public CreateAnswerCommand toCommand() {
        return new CreateAnswerCommand(content);
    }
}