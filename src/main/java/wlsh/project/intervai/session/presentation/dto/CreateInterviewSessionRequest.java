package wlsh.project.intervai.session.presentation.dto;

import jakarta.validation.constraints.NotNull;

public record CreateInterviewSessionRequest(
        @NotNull(message = "면접 ID는 필수입니다.")
        Long interviewId
) {
}
