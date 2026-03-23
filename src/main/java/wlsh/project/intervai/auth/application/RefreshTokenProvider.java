package wlsh.project.intervai.auth.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.auth.domain.RefreshToken;
import wlsh.project.intervai.auth.infra.RefreshTokenEntity;
import wlsh.project.intervai.auth.infra.RefreshTokenRepository;

import java.time.LocalDateTime;

@Component
public class RefreshTokenProvider {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final long expiration;

    public RefreshTokenProvider(
            @Value("${jwt.refresh.secret}") String secret,
            @Value("${jwt.refresh.expiration}") long expiration,
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.jwtProvider = new JwtProvider(secret, expiration);
        this.refreshTokenRepository = refreshTokenRepository;
        this.expiration = expiration;
    }

    public String createToken(Long userId) {
        String tokenString = jwtProvider.createToken(userId);
        LocalDateTime expiresAt = createExpiresAt();

        RefreshToken refreshToken = RefreshToken.create(userId, tokenString, expiresAt);
        refreshTokenRepository.save(RefreshTokenEntity.from(refreshToken));
        return refreshToken.getToken();
    }

    private LocalDateTime createExpiresAt() {
        return LocalDateTime.now().plusSeconds(expiration / 1000);
    }

    public Long parseUserId(String token) {
        return jwtProvider.parseUserId(token);
    }
}
