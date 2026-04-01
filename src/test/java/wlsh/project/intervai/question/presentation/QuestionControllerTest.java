package wlsh.project.intervai.question.presentation;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import wlsh.project.intervai.common.AcceptanceTest;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.question.application.QuestionService;
import wlsh.project.intervai.question.domain.Question;
import wlsh.project.intervai.question.domain.QuestionType;
import wlsh.project.intervai.question.presentation.dto.CreateQuestionRequest;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;

@WebMvcTest(QuestionController.class)
class QuestionControllerTest extends AcceptanceTest {

    @MockitoBean
    private QuestionService questionService;

    @Test
    @DisplayName("질문 생성 성공 시 201과 질문 정보가 반환된다")
    void createQuestion() throws Exception {
        Long userId = 1L;
        Long interviewId = 5L;
        Long sessionId = 10L;
        Question question = Question.of(1L, userId, interviewId, sessionId,
                "[Mock] CS 면접 질문입니다. 난이도: JUNIOR", QuestionType.QUESTION);

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(questionService.create(sessionId)).willReturn(question);

        CreateQuestionRequest request = new CreateQuestionRequest(sessionId);

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(request))
        .when()
                .post("/api/questions")
        .then()
                .statusCode(201)
                .body("id", equalTo(1))
                .body("userId", equalTo(1))
                .body("interviewId", equalTo(5))
                .body("sessionId", equalTo(10))
                .body("content", equalTo("[Mock] CS 면접 질문입니다. 난이도: JUNIOR"));
    }

    @Test
    @DisplayName("이미 종료된 세션으로 질문 생성 시 400이 반환된다")
    void createQuestionWithCompletedSession() throws Exception {
        Long userId = 1L;
        Long sessionId = 10L;

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(questionService.create(sessionId))
                .willThrow(new CustomException(ErrorCode.SESSION_ALREADY_COMPLETED));

        CreateQuestionRequest request = new CreateQuestionRequest(sessionId);

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(request))
        .when()
                .post("/api/questions")
        .then()
                .statusCode(400)
                .body("code", equalTo("SESSION_ALREADY_COMPLETED"));
    }

    @Test
    @DisplayName("질문 수 초과 시 400이 반환된다")
    void createQuestionExceedingCount() throws Exception {
        Long userId = 1L;
        Long sessionId = 10L;

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(questionService.create(sessionId))
                .willThrow(new CustomException(ErrorCode.QUESTION_COUNT_EXCEEDED));

        CreateQuestionRequest request = new CreateQuestionRequest(sessionId);

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(request))
        .when()
                .post("/api/questions")
        .then()
                .statusCode(400)
                .body("code", equalTo("QUESTION_COUNT_EXCEEDED"));
    }

    @Test
    @DisplayName("존재하지 않는 세션으로 질문 생성 시 404가 반환된다")
    void createQuestionWithNonExistentSession() throws Exception {
        Long userId = 1L;
        Long sessionId = 999L;

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(questionService.create(sessionId))
                .willThrow(new CustomException(ErrorCode.SESSION_NOT_FOUND));

        CreateQuestionRequest request = new CreateQuestionRequest(sessionId);

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(request))
        .when()
                .post("/api/questions")
        .then()
                .statusCode(404)
                .body("code", equalTo("SESSION_NOT_FOUND"));
    }

    @Test
    @DisplayName("세션 ID가 없으면 400이 반환된다")
    void createQuestionWithoutSessionId() throws Exception {
        given(accessTokenProvider.parseUserId("valid-token")).willReturn(1L);

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
                .contentType(ContentType.JSON)
                .body("{}")
        .when()
                .post("/api/questions")
        .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("인증 없이 질문 생성 시 403이 반환된다")
    void createQuestionWithoutAuth() throws Exception {
        CreateQuestionRequest request = new CreateQuestionRequest(10L);

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(request))
        .when()
                .post("/api/questions")
        .then()
                .statusCode(403);
    }
}
