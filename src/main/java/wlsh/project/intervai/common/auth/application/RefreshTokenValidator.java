package wlsh.project.intervai.common.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.common.auth.infra.RefreshTokenRedisRepository;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;

@Component
@RequiredArgsConstructor
public class RefreshTokenValidator {

    private final RefreshTokenProvider refreshTokenProvider;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    public Long validate(String token) {
        if (token == null) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Long userId = refreshTokenProvider.parseUserId(token);
        refreshTokenRedisRepository.findTokenByUserId(userId)
                .filter(storedToken -> storedToken.equals(token))
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
        return userId;
    }
}
