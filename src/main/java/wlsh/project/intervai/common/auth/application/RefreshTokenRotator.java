package wlsh.project.intervai.common.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.common.auth.domain.TokenPair;
import wlsh.project.intervai.common.auth.infra.RefreshTokenRedisRepository;

@Component
@RequiredArgsConstructor
public class RefreshTokenRotator {

    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final TokenPairGenerator tokenPairGenerator;

    public TokenPair rotate(Long userId, String oldToken) {
        return tokenPairGenerator.createTokenPair(userId);
    }
}
