package wlsh.project.intervai.user.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wlsh.project.intervai.common.auth.presentation.cookie.RefreshTokenCookieHandler;
import wlsh.project.intervai.user.application.UserService;
import wlsh.project.intervai.user.domain.CreateUserCommand;
import wlsh.project.intervai.user.domain.CreateUserResult;
import wlsh.project.intervai.user.domain.LoginCommand;
import wlsh.project.intervai.user.domain.LoginResult;
import wlsh.project.intervai.user.presentation.dto.CreateUserRequest;
import wlsh.project.intervai.user.presentation.dto.CreateUserResponse;
import wlsh.project.intervai.user.presentation.dto.LoginRequest;
import wlsh.project.intervai.user.presentation.dto.LoginResponse;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RefreshTokenCookieHandler refreshTokenCookieHandler;

    @PostMapping("/sign-up")
    public ResponseEntity<CreateUserResponse> signUp(@RequestBody @Valid CreateUserRequest request) {
        CreateUserCommand command = request.toCommand();
        CreateUserResult result = userService.create(command);

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookieHandler.createRefreshTokenCookie(result.refreshToken()).toString())
                .body(CreateUserResponse.of(result.user(), result.accessToken()));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginCommand command = request.toCommand();
        LoginResult result = userService.login(command);

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookieHandler.createRefreshTokenCookie(result.refreshToken()).toString())
                .body(LoginResponse.of(result.user(), result.accessToken()));
    }
}
