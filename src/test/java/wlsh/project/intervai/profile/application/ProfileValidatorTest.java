package wlsh.project.intervai.profile.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wlsh.project.intervai.common.IntegrationTest;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.profile.domain.CreateProfileCommand;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProfileValidatorTest extends IntegrationTest {

    @Autowired
    private ProfileValidator profileValidator;

    @Autowired
    private ProfileManager profileManager;

    @Test
    @DisplayName("프로필이 없는 경우 validateProfileNotExists는 예외가 발생하지 않는다")
    void validateProfileNotExists() {
        // given
        Long userId = 1L;

        // when & then
        assertThatNoException()
                .isThrownBy(() -> profileValidator.validateProfileNotExists(userId));
    }

    @Test
    @DisplayName("프로필이 이미 존재하는 경우 PROFILE_ALREADY_EXISTS 예외가 발생한다")
    void validateProfileNotExistsAlreadyExists() {
        // given
        Long userId = 1L;
        profileManager.create(userId, CreateProfileCommand.EMPTY_PROFILE);

        // when & then
        assertThatThrownBy(() -> profileValidator.validateProfileNotExists(userId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.PROFILE_ALREADY_EXISTS.getMessage());
    }
}
