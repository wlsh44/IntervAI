package wlsh.project.intervai.profile.presentation;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
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
import wlsh.project.intervai.profile.domain.JobCategory;
import wlsh.project.intervai.profile.domain.Profile;
import wlsh.project.intervai.profile.domain.UpdateProfileCommand;
import wlsh.project.intervai.profile.presentation.dto.UpdateProfileRequest;

import static org.hamcrest.Matchers.equalTo;
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
        Profile profile = Profile.of(profileId, userId, JobCategory.BACKEND, CareerLevel.JUNIOR,
                List.of("Java", "Spring"), List.of("https://github.com/test"));
        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(profileService.updateProfile(eq(userId), eq(profileId), any(UpdateProfileCommand.class)))
                .willReturn(profile);

        UpdateProfileRequest request = new UpdateProfileRequest(
                JobCategory.BACKEND, CareerLevel.JUNIOR,
                List.of("Java", "Spring"), List.of("https://github.com/test"));

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(request))
        .when()
                .put("/api/profile/{profileId}", profileId)
        .then()
                .statusCode(200)
                .body("id", equalTo(10))
                .body("jobCategory", equalTo("BACKEND"))
                .body("careerLevel", equalTo("JUNIOR"))
                .body("techStacks[0]", equalTo("Java"))
                .body("techStacks[1]", equalTo("Spring"))
                .body("portfolioLinks[0]", equalTo("https://github.com/test"));
    }

    @Test
    @DisplayName("본인의 프로필이 아닌 경우 수정 시 403이 반환된다")
    void updateProfileAccessDenied() throws Exception {
        Long userId = 1L;
        Long profileId = 10L;
        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        willThrow(new CustomException(ErrorCode.PROFILE_ACCESS_DENIED))
                .given(profileService).updateProfile(eq(userId), eq(profileId), any(UpdateProfileCommand.class));

        UpdateProfileRequest request = new UpdateProfileRequest(
                JobCategory.BACKEND, CareerLevel.JUNIOR,
                List.of("Java", "Spring"), List.of());

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(request))
        .when()
                .put("/api/profile/{profileId}", profileId)
        .then()
                .statusCode(403)
                .body("code", equalTo("PROFILE_ACCESS_DENIED"))
                .body("message", equalTo("본인의 프로필만 접근할 수 있습니다."));
    }

    @Test
    @DisplayName("프로필 조회 성공 시 200과 프로필 정보가 반환된다")
    void getProfile() throws Exception {
        Long userId = 1L;
        Long profileId = 10L;
        Profile profile = Profile.of(profileId, userId, JobCategory.FRONTEND, CareerLevel.ENTRY,
                List.of("React", "TypeScript"), List.of());
        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(profileService.getProfile(userId, profileId)).willReturn(profile);

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
        .when()
                .get("/api/profile/{profileId}", profileId)
        .then()
                .statusCode(200)
                .body("id", equalTo(10))
                .body("jobCategory", equalTo("FRONTEND"))
                .body("careerLevel", equalTo("ENTRY"))
                .body("techStacks[0]", equalTo("React"))
                .body("techStacks[1]", equalTo("TypeScript"));
    }

    @Test
    @DisplayName("본인의 프로필이 아닌 경우 조회 시 403이 반환된다")
    void getProfileAccessDenied() throws Exception {
        Long userId = 1L;
        Long profileId = 10L;
        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(profileService.getProfile(userId, profileId))
                .willThrow(new CustomException(ErrorCode.PROFILE_ACCESS_DENIED));

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
        .when()
                .get("/api/profile/{profileId}", profileId)
        .then()
                .statusCode(403)
                .body("code", equalTo("PROFILE_ACCESS_DENIED"))
                .body("message", equalTo("본인의 프로필만 접근할 수 있습니다."));
    }

    @Test
    @DisplayName("인증 없이 프로필 조회 시 403이 반환된다")
    void getProfileWithoutAuth() {
        RestAssuredMockMvc.given()
        .when()
                .get("/api/profile/{profileId}", 10L)
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
                .put("/api/profile/{profileId}", 10L)
        .then()
                .statusCode(403);
    }
}
