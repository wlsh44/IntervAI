package wlsh.project.intervai.auth.presentation.dto;

public record TokenRefreshResult(
        String accessToken,
        String refreshToken
) {
}
