package wlsh.project.intervai.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.auth.domain.TokenPair;
import wlsh.project.intervai.auth.infra.RefreshTokenRedisRepository;

@Component
@RequiredArgsConstructor
public class RefreshTokenRotator {

    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final TokenPairGenerator tokenPairGenerator;

    public TokenPair rotate(Long userId, String oldToken) {
        return tokenPairGenerator.createTokenPair(userId);
    }
}
