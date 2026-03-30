package wlsh.project.intervai.user.domain;

public record CreateUserResult(
        User user,
        String accessToken,
        String refreshToken
) {
}
