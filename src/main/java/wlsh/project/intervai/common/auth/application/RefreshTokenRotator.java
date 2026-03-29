package wlsh.project.intervai.common.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.common.auth.domain.TokenPair;

@Component
@RequiredArgsConstructor
public class RefreshTokenRotator {

    private final TokenPairGenerator tokenPairGenerator;

    public TokenPair rotate(Long userId) {
        return tokenPairGenerator.createTokenPair(userId);
    }
}
