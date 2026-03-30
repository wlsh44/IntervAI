package wlsh.project.intervai.user.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import wlsh.project.intervai.user.domain.CreateUserCommand;

public record CreateUserRequest(
        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 4, max = 8, message = "닉네임은 4자 이상 8자 이하여야 합니다.")
        String nickname,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 4, max = 12, message = "비밀번호는 4자 이상 12자 이하여야 합니다.")
        String password
) {

    public CreateUserCommand toCommand() {
        return new CreateUserCommand(nickname, password);
    }
}
