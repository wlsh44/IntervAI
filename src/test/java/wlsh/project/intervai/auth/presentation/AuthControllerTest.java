package wlsh.project.intervai.auth.presentation;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseCookie;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import wlsh.project.intervai.common.auth.application.AuthService;
import wlsh.project.intervai.common.auth.domain.TokenPair;
import wlsh.project.intervai.common.auth.presentation.AuthController;
import wlsh.project.intervai.common.auth.presentation.cookie.RefreshTokenCookieHandler;
import wlsh.project.intervai.common.AcceptanceTest;
import wlsh.project.intervai.common.config.SecurityConfig;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.BDDMockito.given;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest extends AcceptanceTest {

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private RefreshTokenCookieHandler cookieHandler;

    @Test
    @DisplayName("유효한 리프레시 토큰으로 재발급 시 200과 새 토큰이 반환된다")
    void refresh() {
        TokenPair tokenPair = new TokenPair("new-access-token", "new-refresh-token");
        given(authService.refresh("old-refresh-token")).willReturn(tokenPair);
        given(cookieHandler.createRefreshTokenCookie("new-refresh-token"))
                .willReturn(ResponseCookie.from("refresh_token", "new-refresh-token").build());

        RestAssuredMockMvc.given()
                .cookie(new Cookie.Builder("refresh_token", "old-refresh-token").build())
        .when()
                .post("/api/auth/refresh")
        .then()
                .statusCode(200)
                .body("accessToken", equalTo("new-access-token"))
                .header("Set-Cookie", notNullValue());
    }

    @Test
    @DisplayName("리프레시 토큰 쿠키가 없으면 401이 반환된다")
    void refreshWithoutCookie() {
        given(authService.refresh(null))
                .willThrow(new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));
        given(cookieHandler.removeRefreshTokenCookie())
                .willReturn(ResponseCookie.from("refresh_token", "").maxAge(0).build());

        RestAssuredMockMvc.given()
        .when()
                .post("/api/auth/refresh")
        .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("유효하지 않은 리프레시 토큰이면 401이 반환된다")
    void refreshWithInvalidToken() {
        given(authService.refresh("invalid-token"))
                .willThrow(new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));
        given(cookieHandler.removeRefreshTokenCookie())
                .willReturn(ResponseCookie.from("refresh_token", "").maxAge(0).build());

        RestAssuredMockMvc.given()
                .cookie(new Cookie.Builder("refresh_token", "invalid-token").build())
        .when()
                .post("/api/auth/refresh")
        .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("만료된 리프레시 토큰이면 401이 반환된다")
    void refreshWithExpiredToken() {
        given(authService.refresh("expired-token"))
                .willThrow(new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));
        given(cookieHandler.removeRefreshTokenCookie())
                .willReturn(ResponseCookie.from("refresh_token", "").maxAge(0).build());

        RestAssuredMockMvc.given()
                .cookie(new Cookie.Builder("refresh_token", "expired-token").build())
        .when()
                .post("/api/auth/refresh")
        .then()
                .statusCode(401);
    }
}
