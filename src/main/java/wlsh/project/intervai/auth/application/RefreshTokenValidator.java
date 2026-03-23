package wlsh.project.intervai.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.auth.domain.RefreshToken;
import wlsh.project.intervai.auth.infra.RefreshTokenEntity;
import wlsh.project.intervai.auth.infra.RefreshTokenRepository;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;

@Component
@RequiredArgsConstructor
public class RefreshTokenValidator {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken validate(String token) {
        if (token == null) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .map(RefreshTokenEntity::toDomain)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.deleteByToken(token);
            throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        return refreshToken;
    }
}
