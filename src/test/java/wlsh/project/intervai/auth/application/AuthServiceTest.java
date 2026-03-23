package wlsh.project.intervai.auth.application;

import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wlsh.project.intervai.auth.domain.TokenPair;
import wlsh.project.intervai.auth.infra.RefreshTokenRedisRepository;
import wlsh.project.intervai.common.IntegrationTest;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthServiceTest extends IntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private RefreshTokenProvider refreshTokenProvider;

    @Autowired
    private RefreshTokenRedisRepository refreshTokenRedisRepository;

    @Test
    @DisplayName("유효한 리프레시 토큰으로 재발급하면 새로운 토큰 쌍이 반환되고 리프레시 토큰이 Redis에 저장된다")
    void refresh() {
        // given
        String oldToken = refreshTokenProvider.createToken(1L);
        refreshTokenRedisRepository.save(1L, oldToken, Duration.ofDays(7));

        // when
        TokenPair result = authService.refresh(oldToken);

        // then
        String storedToken = refreshTokenRedisRepository.findTokenByUserId(1L).orElseThrow();
        assertThat(result.accessToken()).isNotBlank();
        assertThat(result.refreshToken()).isNotBlank();
        assertThat(storedToken).isEqualTo(result.refreshToken());
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
        String token = refreshTokenProvider.createToken(1L);

        assertThatThrownBy(() -> authService.refresh(token))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.REFRESH_TOKEN_NOT_FOUND.getMessage());
    }
}
