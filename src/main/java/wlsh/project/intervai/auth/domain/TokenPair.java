package wlsh.project.intervai.auth.domain;

public record TokenPair(
        String accessToken,
        String refreshToken
) {
}
