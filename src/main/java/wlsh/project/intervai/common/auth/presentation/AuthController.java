package wlsh.project.intervai.common.auth.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wlsh.project.intervai.common.auth.application.AuthService;
import wlsh.project.intervai.common.auth.domain.TokenPair;
import wlsh.project.intervai.common.auth.presentation.cookie.RefreshTokenCookieHandler;
import wlsh.project.intervai.common.auth.presentation.dto.TokenRefreshResponse;
import wlsh.project.intervai.common.exception.CustomException;

import static wlsh.project.intervai.common.auth.presentation.cookie.RefreshTokenCookieHandler.REFRESH_TOKEN_COOKIE_NAME;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenCookieHandler cookieHandler;

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refresh(
            @CookieValue(name = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken
    ) {
        try {
            TokenPair result = authService.refresh(refreshToken);
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookieHandler.createRefreshTokenCookie(result.refreshToken()).toString())
                    .body(new TokenRefreshResponse(result.accessToken()));
        } catch (CustomException e) {
            cookieHandler.removeRefreshTokenCookie();
            throw e;
        }
    }
}
