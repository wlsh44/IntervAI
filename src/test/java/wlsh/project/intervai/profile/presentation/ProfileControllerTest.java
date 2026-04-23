package wlsh.project.intervai.profile.presentation;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import wlsh.project.intervai.common.AcceptanceTest;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.profile.application.ProfileService;
import wlsh.project.intervai.profile.domain.CareerLevel;
import wlsh.project.intervai.common.domain.JobCategory;
import wlsh.project.intervai.profile.domain.Profile;
import wlsh.project.intervai.profile.domain.UpdateProfileCommand;
import wlsh.project.intervai.profile.presentation.dto.UpdateProfileRequest;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;

@WebMvcTest(ProfileController.class)
class ProfileControllerTest extends AcceptanceTest {

    @MockitoBean
    private ProfileService profileService;

    @Test
    @DisplayName("프로필 수정 성공 시 200과 프로필 정보가 반환된다")
    void updateProfile() throws Exception {
        Long userId = 1L;
        Long profileId = 10L;
        LocalDateTime updatedAt = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        Profile profile = Profile.of(profileId, userId, JobCategory.BACKEND, CareerLevel.JUNIOR,
                List.of("Java", "Spring"), List.of("https://github.com/test"), updatedAt);
        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(profileService.updateProfile(eq(userId), any(UpdateProfileCommand.class)))
                .willReturn(profile);

        UpdateProfileRequest request = new UpdateProfileRequest(
                JobCategory.BACKEND, CareerLevel.JUNIOR,
                List.of("Java", "Spring"), List.of("https://github.com/test"));

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(request))
        .when()
                .put("/api/users/profile")
        .then()
                .statusCode(200)
                .body("id", equalTo(10))
                .body("jobCategory", equalTo("BACKEND"))
                .body("careerLevel", equalTo("JUNIOR"))
                .body("techStacks[0]", equalTo("Java"))
                .body("techStacks[1]", equalTo("Spring"))
                .body("portfolioLinks[0]", equalTo("https://github.com/test"))
                .body("updatedAt", notNullValue());
    }

    @Test
    @DisplayName("프로필 조회 성공 시 200과 프로필 정보가 반환된다")
    void getProfile() {
        Long userId = 1L;
        Long profileId = 10L;
        LocalDateTime updatedAt = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        Profile profile = Profile.of(profileId, userId, JobCategory.FRONTEND, CareerLevel.ENTRY,
                List.of("React", "TypeScript"), List.of(), updatedAt);
        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(profileService.getProfile(userId)).willReturn(profile);

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
        .when()
                .get("/api/users/profile")
        .then()
                .statusCode(200)
                .body("id", equalTo(10))
                .body("jobCategory", equalTo("FRONTEND"))
                .body("careerLevel", equalTo("ENTRY"))
                .body("techStacks[0]", equalTo("React"))
                .body("techStacks[1]", equalTo("TypeScript"))
                .body("updatedAt", notNullValue());
    }

    @Test
    @DisplayName("프로필이 존재하지 않으면 조회 시 404가 반환된다")
    void getProfileNotFound() {
        Long userId = 1L;
        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        willThrow(new CustomException(ErrorCode.PROFILE_NOT_FOUND))
                .given(profileService).getProfile(userId);

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
        .when()
                .get("/api/users/profile")
        .then()
                .statusCode(404)
                .body("code", equalTo("PROFILE_NOT_FOUND"));
    }

    @Test
    @DisplayName("인증 없이 프로필 조회 시 403이 반환된다")
    void getProfileWithoutAuth() {
        RestAssuredMockMvc.given()
        .when()
                .get("/api/users/profile")
        .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("인증 없이 프로필 수정 시 403이 반환된다")
    void updateProfileWithoutAuth() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest(
                JobCategory.BACKEND, CareerLevel.JUNIOR,
                List.of("Java"), List.of());

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(request))
        .when()
                .put("/api/users/profile")
        .then()
                .statusCode(403);
    }
}
