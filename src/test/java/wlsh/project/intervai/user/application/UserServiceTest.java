package wlsh.project.intervai.user.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wlsh.project.intervai.common.IntegrationTest;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.user.domain.CreateUserCommand;
import wlsh.project.intervai.user.domain.CreateUserResult;
import wlsh.project.intervai.user.domain.LoginCommand;
import wlsh.project.intervai.user.domain.LoginResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserServiceTest extends IntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("유저를 생성하면 유저 정보와 토큰이 반환된다")
    void create() {
        // given
        CreateUserCommand command = new CreateUserCommand("testuser", "pass1234");

        // when
        CreateUserResult result = userService.create(command);

        // then
        assertThat(result.user().getId()).isNotNull();
        assertThat(result.user().getNickname()).isEqualTo("testuser");
        assertThat(result.user().getPasswordHash()).isNotEqualTo("pass1234");
        assertThat(result.accessToken()).isNotBlank();
        assertThat(result.refreshToken()).isNotBlank();
    }

    @Test
    @DisplayName("중복된 닉네임으로 유저를 생성하면 예외가 발생한다")
    void createWithDuplicateNickname() {
        // given
        CreateUserCommand command = new CreateUserCommand("dupl", "pass1234");
        userService.create(command);

        // when & then
        CreateUserCommand duplicateCommand = new CreateUserCommand("dupl", "pass4567");
        assertThatThrownBy(() -> userService.create(duplicateCommand))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.DUPLICATE_NICKNAME.getMessage());
    }

    @Test
    @DisplayName("서로 다른 유저를 생성하면 각각 다른 토큰이 발급된다")
    void createMultipleUsers() {
        // given
        CreateUserCommand command1 = new CreateUserCommand("user1234", "pass1234");
        CreateUserCommand command2 = new CreateUserCommand("user5678", "pass1234");

        // when
        CreateUserResult result1 = userService.create(command1);
        CreateUserResult result2 = userService.create(command2);

        // then
        assertThat(result1.user().getId()).isNotEqualTo(result2.user().getId());
        assertThat(result1.accessToken()).isNotEqualTo(result2.accessToken());
        assertThat(result1.refreshToken()).isNotEqualTo(result2.refreshToken());
    }

    @Test
    @DisplayName("회원가입 후 로그인 성공 시 유저 정보와 토큰이 반환된다")
    void login() {
        // given
        CreateUserCommand createCommand = new CreateUserCommand("login", "pass1234");
        userService.create(createCommand);

        LoginCommand loginCommand = new LoginCommand("login", "pass1234");

        // when
        LoginResult result = userService.login(loginCommand);

        // then
        assertThat(result.user().getId()).isNotNull();
        assertThat(result.user().getNickname()).isEqualTo("login");
        assertThat(result.accessToken()).isNotBlank();
        assertThat(result.refreshToken()).isNotBlank();
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시 예외가 발생한다")
    void loginWithWrongPassword() {
        // given
        CreateUserCommand createCommand = new CreateUserCommand("wrong", "pass1234");
        userService.create(createCommand);

        LoginCommand loginCommand = new LoginCommand("wrong", "wrongpass");

        // when & then
        assertThatThrownBy(() -> userService.login(loginCommand))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.LOGIN_FAILED.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 닉네임으로 로그인 시 예외가 발생한다")
    void loginWithNonExistentNickname() {
        // given
        LoginCommand loginCommand = new LoginCommand("nono", "pass1234");

        // when & then
        assertThatThrownBy(() -> userService.login(loginCommand))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.LOGIN_FAILED.getMessage());
    }
}
