package wlsh.project.intervai.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wlsh.project.intervai.auth.domain.RefreshToken;
import wlsh.project.intervai.auth.domain.TokenPair;
import wlsh.project.intervai.auth.presentation.dto.TokenRefreshResult;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenValidator refreshTokenValidator;
    private final RefreshTokenRotator refreshTokenRotator;

    public TokenPair refresh(String refreshTokenValue) {
        RefreshToken oldToken = refreshTokenValidator.validate(refreshTokenValue);

        return refreshTokenRotator.rotate(refreshTokenValue, oldToken.getUserId());
    }
}
