package wlsh.project.intervai.common.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wlsh.project.intervai.common.auth.domain.TokenPair;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenValidator refreshTokenValidator;
    private final RefreshTokenRotator refreshTokenRotator;

    public TokenPair refresh(String oldToken) {
        Long userId = refreshTokenValidator.validate(oldToken);

        return refreshTokenRotator.rotate(userId);
    }
}
