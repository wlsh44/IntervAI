package wlsh.project.intervai.auth.application;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.auth.infra.RefreshTokenRedisRepository;

@Component
public class RefreshTokenProvider {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final long expiration;

    public RefreshTokenProvider(
            @Value("${jwt.refresh.secret}") String secret,
            @Value("${jwt.refresh.expiration}") long expiration,
            RefreshTokenRedisRepository refreshTokenRedisRepository
    ) {
        this.jwtProvider = new JwtProvider(secret, expiration);
        this.refreshTokenRedisRepository = refreshTokenRedisRepository;
        this.expiration = expiration;
    }

    public String createToken(Long userId) {
        return jwtProvider.createToken(userId);
    }

    public String createAndSaveToken(Long userId) {
        String tokenString = jwtProvider.createToken(userId);
        refreshTokenRedisRepository.save(userId, tokenString, Duration.ofMillis(expiration));
        return tokenString;
    }

    public Long parseUserId(String token) {
        return jwtProvider.parseUserId(token);
    }
}
