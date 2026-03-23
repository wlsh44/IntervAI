package wlsh.project.intervai.common.auth.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AccessTokenProvider {

    private final JwtProvider jwtProvider;

    public AccessTokenProvider(
            @Value("${jwt.access.secret}") String secret,
            @Value("${jwt.access.expiration}") long expiration
    ) {
        this.jwtProvider = new JwtProvider(secret, expiration);
    }

    public String createToken(Long userId) {
        return jwtProvider.createToken(userId);
    }

    public Long parseUserId(String token) {
        return jwtProvider.parseUserId(token);
    }
}
