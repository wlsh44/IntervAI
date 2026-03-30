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
import wlsh.project.intervai.profile.domain.UpdateProfileCommand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProfileServiceTest extends IntegrationTest {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ProfileManager profileManager;

    @Test
    @DisplayName("본인 프로필을 수정하면 수정된 프로필이 반환된다")
    void updateProfile() {
        // given
        Long userId = 1L;
        CreateProfileCommand createCommand = new CreateProfileCommand(
                JobCategory.BACKEND, CareerLevel.JUNIOR, List.of("Java"), List.of());
        Profile created = profileManager.create(userId, createCommand);

        UpdateProfileCommand updateCommand = new UpdateProfileCommand(
                JobCategory.FRONTEND, CareerLevel.SENIOR, List.of("React", "TypeScript"), List.of("https://github.com/test"));

        // when
        Profile updated = profileService.updateProfile(userId, created.getId(), updateCommand);

        // then
        assertThat(updated.getJobCategory()).isEqualTo(JobCategory.FRONTEND);
        assertThat(updated.getCareerLevel()).isEqualTo(CareerLevel.SENIOR);
        assertThat(updated.getTechStacks()).containsExactly("React", "TypeScript");
        assertThat(updated.getPortfolioLinks()).containsExactly("https://github.com/test");
    }

    @Test
    @DisplayName("타인의 프로필을 수정하면 예외가 발생한다")
    void updateProfileAccessDenied() {
        // given
        Long ownerId = 1L;
        Long otherUserId = 2L;
        CreateProfileCommand createCommand = new CreateProfileCommand(
                JobCategory.BACKEND, CareerLevel.JUNIOR, List.of("Java"), List.of());
        Profile created = profileManager.create(ownerId, createCommand);

        UpdateProfileCommand updateCommand = new UpdateProfileCommand(
                JobCategory.FRONTEND, CareerLevel.SENIOR, List.of("React"), List.of());

        // when & then
        assertThatThrownBy(() -> profileService.updateProfile(otherUserId, created.getId(), updateCommand))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.PROFILE_ACCESS_DENIED.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 프로필을 수정하면 예외가 발생한다")
    void updateProfileNotFound() {
        // given
        Long userId = 1L;
        Long nonExistentProfileId = 999L;
        UpdateProfileCommand updateCommand = new UpdateProfileCommand(
                JobCategory.BACKEND, CareerLevel.JUNIOR, List.of("Java"), List.of());

        // when & then
        assertThatThrownBy(() -> profileService.updateProfile(userId, nonExistentProfileId, updateCommand))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.PROFILE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("본인 프로필을 조회하면 프로필 정보가 반환된다")
    void getProfile() {
        // given
        Long userId = 1L;
        CreateProfileCommand createCommand = new CreateProfileCommand(
                JobCategory.BACKEND, CareerLevel.ENTRY, List.of("Java", "Spring"), List.of("https://github.com/test"));
        Profile created = profileManager.create(userId, createCommand);

        // when
        Profile found = profileService.getProfile(userId, created.getId());

        // then
        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getJobCategory()).isEqualTo(JobCategory.BACKEND);
        assertThat(found.getCareerLevel()).isEqualTo(CareerLevel.ENTRY);
        assertThat(found.getTechStacks()).containsExactly("Java", "Spring");
        assertThat(found.getPortfolioLinks()).containsExactly("https://github.com/test");
    }

    @Test
    @DisplayName("타인의 프로필을 조회하면 예외가 발생한다")
    void getProfileAccessDenied() {
        // given
        Long ownerId = 1L;
        Long otherUserId = 2L;
        CreateProfileCommand createCommand = new CreateProfileCommand(
                JobCategory.BACKEND, CareerLevel.JUNIOR, List.of("Java"), List.of());
        Profile created = profileManager.create(ownerId, createCommand);

        // when & then
        assertThatThrownBy(() -> profileService.getProfile(otherUserId, created.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.PROFILE_ACCESS_DENIED.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 프로필을 조회하면 예외가 발생한다")
    void getProfileNotFound() {
        // given
        Long userId = 1L;
        Long nonExistentProfileId = 999L;

        // when & then
        assertThatThrownBy(() -> profileService.getProfile(userId, nonExistentProfileId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.PROFILE_NOT_FOUND.getMessage());
    }
}
