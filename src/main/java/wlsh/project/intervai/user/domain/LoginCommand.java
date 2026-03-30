package wlsh.project.intervai.user.domain;

public record LoginCommand(
        String nickname,
        String password
) {
}
