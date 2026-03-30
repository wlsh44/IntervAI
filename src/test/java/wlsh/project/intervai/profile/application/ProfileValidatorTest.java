package wlsh.project.intervai.profile.application;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wlsh.project.intervai.common.IntegrationTest;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.profile.domain.CareerLevel;
import wlsh.project.intervai.profile.domain.CreateProfileCommand;
import wlsh.project.intervai.profile.domain.JobCategory;
import wlsh.project.intervai.profile.domain.Profile;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProfileValidatorTest extends IntegrationTest {

    @Autowired
    private ProfileValidator profileValidator;

    @Autowired
    private ProfileManager profileManager;

    @Test
    @DisplayName("본인의 프로필이면 예외가 발생하지 않는다")
    void validateProfileOwner() {
        // given
        Long userId = 1L;
        CreateProfileCommand command = new CreateProfileCommand(
                JobCategory.BACKEND, CareerLevel.JUNIOR, List.of("Java"), List.of());
        Profile profile = profileManager.create(userId, command);

        // when & then
        assertThatNoException()
                .isThrownBy(() -> profileValidator.validateProfileOwner(profile.getId(), userId));
    }

    @Test
    @DisplayName("타인의 프로필이면 PROFILE_ACCESS_DENIED 예외가 발생한다")
    void validateProfileOwnerAccessDenied() {
        // given
        Long ownerId = 1L;
        Long otherUserId = 2L;
        CreateProfileCommand command = new CreateProfileCommand(
                JobCategory.BACKEND, CareerLevel.JUNIOR, List.of("Java"), List.of());
        Profile profile = profileManager.create(ownerId, command);

        // when & then
        assertThatThrownBy(() -> profileValidator.validateProfileOwner(profile.getId(), otherUserId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.PROFILE_ACCESS_DENIED.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 프로필이면 PROFILE_NOT_FOUND 예외가 발생한다")
    void validateProfileOwnerNotFound() {
        // given
        Long nonExistentProfileId = 999L;
        Long userId = 1L;

        // when & then
        assertThatThrownBy(() -> profileValidator.validateProfileOwner(nonExistentProfileId, userId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.PROFILE_NOT_FOUND.getMessage());
    }
}
