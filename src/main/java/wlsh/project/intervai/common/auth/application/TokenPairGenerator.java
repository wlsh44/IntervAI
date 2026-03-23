package wlsh.project.intervai.common.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.common.auth.domain.TokenPair;

@Component
@RequiredArgsConstructor
public class TokenPairGenerator {

    private final AccessTokenProvider accessTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;

    public TokenPair createTokenPair(Long userId) {
        String accessToken = accessTokenProvider.createToken(userId);
        String refreshToken = refreshTokenProvider.createAndSaveToken(userId);

        return new TokenPair(accessToken, refreshToken);
    }
}
