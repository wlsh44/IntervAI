package wlsh.project.intervai.common.auth.domain;

public record TokenPair(
        String accessToken,
        String refreshToken
) {
}
