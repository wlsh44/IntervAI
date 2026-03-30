package wlsh.project.intervai.user.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import wlsh.project.intervai.common.IntegrationTest;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserValidatorTest extends IntegrationTest {

    @Autowired
    private UserValidator userValidator;

    @ParameterizedTest
    @ValueSource(strings = {"abc", "ab", "a"})
    @DisplayName("닉네임이 4자 미만이면 예외가 발생한다")
    void nicknameTooShort(String nickname) {
        assertThatThrownBy(() -> userValidator.validateCreateUser(nickname, "pass1234"))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_NICKNAME_LENGTH.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcdefghi", "abcdefghij"})
    @DisplayName("닉네임이 8자 초과이면 예외가 발생한다")
    void nicknameTooLong(String nickname) {
        assertThatThrownBy(() -> userValidator.validateCreateUser(nickname, "pass1234"))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_NICKNAME_LENGTH.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcd", "abcde", "abcdefgh"})
    @DisplayName("닉네임이 4~8자이면 검증을 통과한다")
    void nicknameValidLength(String nickname) {
        assertThatNoException()
                .isThrownBy(() -> userValidator.validateCreateUser(nickname, "pass1234"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "ab", "a"})
    @DisplayName("비밀번호가 4자 미만이면 예외가 발생한다")
    void passwordTooShort(String password) {
        assertThatThrownBy(() -> userValidator.validateCreateUser("testuser", password))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_PASSWORD_LENGTH.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcdefghijklm", "abcdefghijklmn"})
    @DisplayName("비밀번호가 12자 초과이면 예외가 발생한다")
    void passwordTooLong(String password) {
        assertThatThrownBy(() -> userValidator.validateCreateUser("testuser", password))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_PASSWORD_LENGTH.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcd", "abcdef", "abcdefghijkl"})
    @DisplayName("비밀번호가 4~12자이면 검증을 통과한다")
    void passwordValidLength(String password) {
        assertThatNoException()
                .isThrownBy(() -> userValidator.validateCreateUser("testuser", password));
    }

    @Test
    @DisplayName("닉네임과 비밀번호 모두 유효하면 검증을 통과한다")
    void validNicknameAndPassword() {
        assertThatNoException()
                .isThrownBy(() -> userValidator.validateCreateUser("testuser", "pass1234"));
    }
}
