package wlsh.project.intervai.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import wlsh.project.intervai.auth.domain.TokenPair;
import wlsh.project.intervai.auth.infra.RefreshTokenRepository;

@Component
@RequiredArgsConstructor
public class RefreshTokenRotator {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenPairGenerator tokenPairGenerator;

    @Transactional
    public TokenPair rotate(String oldToken, Long userId) {
        refreshTokenRepository.deleteByToken(oldToken);
        return tokenPairGenerator.createTokenPair(userId);
    }
}
