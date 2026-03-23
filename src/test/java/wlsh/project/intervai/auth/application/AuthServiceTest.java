package wlsh.project.intervai.auth.application;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wlsh.project.intervai.auth.domain.RefreshToken;
import wlsh.project.intervai.auth.domain.TokenPair;
import wlsh.project.intervai.auth.infra.RefreshTokenEntity;
import wlsh.project.intervai.auth.infra.RefreshTokenRepository;
import wlsh.project.intervai.common.IntegrationTest;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthServiceTest extends IntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("유효한 리프레시 토큰으로 재발급하면 새로운 토큰 쌍이 반환된다")
    void refresh() {
        // given
        RefreshToken refreshToken = RefreshToken.create(1L, "valid-token", LocalDateTime.now().plusDays(7));
        refreshTokenRepository.save(RefreshTokenEntity.from(refreshToken));

        // when
        TokenPair result = authService.refresh("valid-token");

        // then
        assertThat(result.accessToken()).isNotBlank();
        assertThat(result.refreshToken()).isNotBlank();
        assertThat(result.refreshToken()).isNotEqualTo("valid-token");
    }

    @Test
    @DisplayName("재발급 후 기존 리프레시 토큰은 삭제된다")
    void refreshDeletesOldToken() {
        // given
        RefreshToken refreshToken = RefreshToken.create(1L, "old-token", LocalDateTime.now().plusDays(7));
        refreshTokenRepository.save(RefreshTokenEntity.from(refreshToken));

        // when
        authService.refresh("old-token");

        // then
        assertThat(refreshTokenRepository.findByToken("old-token")).isEmpty();
    }

    @Test
    @DisplayName("재발급 후 새로운 리프레시 토큰이 DB에 저장된다")
    void refreshSavesNewToken() {
        // given
        RefreshToken refreshToken = RefreshToken.create(1L, "old-token", LocalDateTime.now().plusDays(7));
        refreshTokenRepository.save(RefreshTokenEntity.from(refreshToken));

        // when
        TokenPair result = authService.refresh("old-token");

        // then
        assertThat(refreshTokenRepository.findByToken(result.refreshToken())).isPresent();
    }

    @Test
    @DisplayName("null 리프레시 토큰이면 예외가 발생한다")
    void refreshWithNull() {
        assertThatThrownBy(() -> authService.refresh(null))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_REFRESH_TOKEN.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 리프레시 토큰이면 예외가 발생한다")
    void refreshWithInvalidToken() {
        assertThatThrownBy(() -> authService.refresh("non-existent-token"))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_REFRESH_TOKEN.getMessage());
    }

    @Test
    @DisplayName("만료된 리프레시 토큰이면 예외가 발생한다")
    void refreshWithExpiredToken() {
        // given
        RefreshToken refreshToken = RefreshToken.create(1L, "expired-token", LocalDateTime.now().minusDays(1));
        refreshTokenRepository.save(RefreshTokenEntity.from(refreshToken));

        // when & then
        assertThatThrownBy(() -> authService.refresh("expired-token"))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.EXPIRED_REFRESH_TOKEN.getMessage());
    }
}
