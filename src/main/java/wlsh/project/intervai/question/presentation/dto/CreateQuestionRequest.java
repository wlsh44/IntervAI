package wlsh.project.intervai.question.presentation.dto;

import jakarta.validation.constraints.NotNull;

public record CreateQuestionRequest(
        @NotNull(message = "세션 ID는 필수입니다.")
        Long sessionId
) {
}
