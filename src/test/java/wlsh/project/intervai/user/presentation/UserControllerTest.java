package wlsh.project.intervai.user.presentation;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseCookie;
import wlsh.project.intervai.auth.presentation.cookie.RefreshTokenCookieHandler;
import wlsh.project.intervai.common.AcceptanceTest;
import wlsh.project.intervai.common.config.SecurityConfig;
import wlsh.project.intervai.user.application.UserService;
import wlsh.project.intervai.user.domain.CreateUserCommand;
import wlsh.project.intervai.user.domain.CreateUserResult;
import wlsh.project.intervai.user.domain.User;
import wlsh.project.intervai.user.presentation.dto.CreateUserRequest;

import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest extends AcceptanceTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private RefreshTokenCookieHandler refreshTokenCookieHandler;

    @Test
    @DisplayName("회원가입 성공 시 200과 유저 정보, 토큰이 반환된다")
    void signUp() throws Exception {
        User user = User.of(1L, "test", "encodedPassword");
        CreateUserResult result = new CreateUserResult(user, "access-token", "refresh-token");
        given(userService.create(any(CreateUserCommand.class))).willReturn(result);
        given(refreshTokenCookieHandler.createRefreshTokenCookie("refresh-token"))
                .willReturn(ResponseCookie.from("refresh_token", "refresh-token").build());

        CreateUserRequest request = new CreateUserRequest("test", "test1234");

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(request))
        .when()
                .post("/api/users/sign-up")
        .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("nickname", equalTo("test"))
                .body("accessToken", equalTo("access-token"))
                .header("Set-Cookie", notNullValue());
    }

    @Test
    @DisplayName("중복 닉네임으로 회원가입 시 400과 에러 메시지가 반환된다")
    void signUpDuplicateNickname() throws Exception {
        given(userService.create(any(CreateUserCommand.class)))
                .willThrow(new CustomException(ErrorCode.DUPLICATE_NICKNAME));

        CreateUserRequest request = new CreateUserRequest("test", "test1234");

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(request))
        .when()
                .post("/api/users/sign-up")
        .then()
                .statusCode(400)
                .body("code", equalTo("DUPLICATE_NICKNAME"))
                .body("message", equalTo("이미 사용 중인 닉네임입니다."));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    @DisplayName("닉네임이 비어있으면 400이 반환된다")
    void signUpBlankNickname(String nickname) throws Exception {
        String body = nickname == null
                ? "{\"password\":\"pass1234\"}"
                : mapper.writeValueAsString(new CreateUserRequest(nickname, "pass1234"));

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(body)
        .when()
                .post("/api/users/sign-up")
        .then()
                .statusCode(400);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    @DisplayName("비밀번호가 비어있으면 400이 반환된다")
    void signUpBlankPassword(String password) throws Exception {
        String body = password == null
                ? "{\"nickname\":\"testuser\"}"
                : mapper.writeValueAsString(new CreateUserRequest("testuser", password));

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(body)
        .when()
                .post("/api/users/sign-up")
        .then()
                .statusCode(400);
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "ab", "a"})
    @DisplayName("닉네임이 4자 미만이면 400이 반환된다")
    void signUpNicknameTooShort(String nickname) throws Exception {
        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(new CreateUserRequest(nickname, "pass1234")))
        .when()
                .post("/api/users/sign-up")
        .then()
                .statusCode(400);
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcdefghi", "abcdefghij"})
    @DisplayName("닉네임이 8자 초과이면 400이 반환된다")
    void signUpNicknameTooLong(String nickname) throws Exception {
        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(new CreateUserRequest(nickname, "pass1234")))
        .when()
                .post("/api/users/sign-up")
        .then()
                .statusCode(400);
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "ab", "a"})
    @DisplayName("비밀번호가 4자 미만이면 400이 반환된다")
    void signUpPasswordTooShort(String password) throws Exception {
        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(new CreateUserRequest("testuser", password)))
        .when()
                .post("/api/users/sign-up")
        .then()
                .statusCode(400);
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcdefghijklm", "abcdefghijklmn"})
    @DisplayName("비밀번호가 12자 초과이면 400이 반환된다")
    void signUpPasswordTooLong(String password) throws Exception {
        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(new CreateUserRequest("testuser", password)))
        .when()
                .post("/api/users/sign-up")
        .then()
                .statusCode(400);
    }
}
