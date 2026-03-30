package wlsh.project.intervai.user.domain;

public record CreateUserCommand(
        String nickname,
        String password
) {
}
