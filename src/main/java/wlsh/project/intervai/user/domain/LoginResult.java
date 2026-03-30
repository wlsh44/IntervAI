package wlsh.project.intervai.user.domain;

public record LoginResult(
        User user,
        String accessToken,
        String refreshToken
) {
}
