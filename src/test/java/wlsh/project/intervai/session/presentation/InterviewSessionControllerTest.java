package wlsh.project.intervai.session.presentation;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import wlsh.project.intervai.common.AcceptanceTest;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.session.application.InterviewSessionService;
import wlsh.project.intervai.session.domain.InterviewSession;
import wlsh.project.intervai.session.domain.InterviewSessionStatus;
import wlsh.project.intervai.session.presentation.dto.CreateInterviewSessionRequest;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;

@WebMvcTest(InterviewSessionController.class)
class InterviewSessionControllerTest extends AcceptanceTest {

    @MockitoBean
    private InterviewSessionService interviewSessionService;

    @Test
    @DisplayName("면접 세션 생성 성공 시 201과 세션 정보가 반환된다")
    void createSession() throws Exception {
        Long userId = 1L;
        Long interviewId = 10L;
        InterviewSession session = InterviewSession.of(1L, interviewId, userId,
                InterviewSessionStatus.IN_PROGRESS, 0, null);

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(interviewSessionService.create(userId, interviewId)).willReturn(session);

        CreateInterviewSessionRequest request = new CreateInterviewSessionRequest(interviewId);

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(request))
        .when()
                .post("/api/interview-sessions")
        .then()
                .statusCode(201)
                .body("id", equalTo(1))
                .body("interviewId", equalTo(10))
                .body("sessionStatus", equalTo("IN_PROGRESS"))
                .body("currentQuestionCount", equalTo(0));
    }

    @Test
    @DisplayName("존재하지 않는 면접으로 세션 생성 시 404가 반환된다")
    void createSessionWithNonExistentInterview() throws Exception {
        Long userId = 1L;
        Long interviewId = 999L;

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(interviewSessionService.create(userId, interviewId))
                .willThrow(new CustomException(ErrorCode.INTERVIEW_NOT_FOUND));

        CreateInterviewSessionRequest request = new CreateInterviewSessionRequest(interviewId);

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(request))
        .when()
                .post("/api/interview-sessions")
        .then()
                .statusCode(404)
                .body("code", equalTo("INTERVIEW_NOT_FOUND"));
    }

    @Test
    @DisplayName("타인의 면접으로 세션 생성 시 403이 반환된다")
    void createSessionWithOtherUserInterview() throws Exception {
        Long userId = 1L;
        Long interviewId = 10L;

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(interviewSessionService.create(userId, interviewId))
                .willThrow(new CustomException(ErrorCode.INTERVIEW_ACCESS_DENIED));

        CreateInterviewSessionRequest request = new CreateInterviewSessionRequest(interviewId);

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(request))
        .when()
                .post("/api/interview-sessions")
        .then()
                .statusCode(403)
                .body("code", equalTo("INTERVIEW_ACCESS_DENIED"));
    }

    @Test
    @DisplayName("면접 ID가 없으면 400이 반환된다")
    void createSessionWithoutInterviewId() throws Exception {
        given(accessTokenProvider.parseUserId("valid-token")).willReturn(1L);

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
                .contentType(ContentType.JSON)
                .body("{}")
        .when()
                .post("/api/interview-sessions")
        .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("인증 없이 면접 세션 생성 시 403이 반환된다")
    void createSessionWithoutAuth() throws Exception {
        CreateInterviewSessionRequest request = new CreateInterviewSessionRequest(10L);

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(request))
        .when()
                .post("/api/interview-sessions")
        .then()
                .statusCode(403);
    }
}
