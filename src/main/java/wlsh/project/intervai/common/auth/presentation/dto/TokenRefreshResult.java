package wlsh.project.intervai.common.auth.presentation.dto;

public record TokenRefreshResult(
        String accessToken,
        String refreshToken
) {
}
