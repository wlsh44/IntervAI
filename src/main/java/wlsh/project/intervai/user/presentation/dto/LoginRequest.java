package wlsh.project.intervai.user.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import wlsh.project.intervai.user.domain.LoginCommand;

public record LoginRequest(
        @NotBlank(message = "닉네임은 필수입니다.")
        String nickname,

        @NotBlank(message = "비밀번호는 필수입니다.")
        String password
) {

    public LoginCommand toCommand() {
        return new LoginCommand(nickname, password);
    }
}
