package wlsh.project.intervai.question.presentation;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import wlsh.project.intervai.common.AcceptanceTest;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.interview.domain.NextQuestionResult;
import wlsh.project.intervai.question.application.QuestionService;
import wlsh.project.intervai.question.domain.Question;
import wlsh.project.intervai.question.domain.QuestionType;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@WebMvcTest(QuestionController.class)
class QuestionControllerTest extends AcceptanceTest {

    @MockitoBean
    private QuestionService questionService;

    @Test
    @DisplayName("현재 본 질문 조회 성공 시 200과 질문 정보가 반환된다")
    void currentMainQuestion() {
        Long userId = 1L;
        Long interviewId = 10L;
        Question question = Question.of(5L, interviewId, 1L, "운영체제란 무엇인가요?", QuestionType.QUESTION, 0);
        NextQuestionResult result = new NextQuestionResult(question, true);

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(questionService.currentQuestion(eq(userId), eq(interviewId))).willReturn(result);

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
        .when()
                .get("/api/interviews/{interviewId}/questions/current", interviewId)
        .then()
                .statusCode(200)
                .body("questionId", equalTo(5))
                .body("question", equalTo("운영체제란 무엇인가요?"))
                .body("questionType", equalTo("QUESTION"))
                .body("hasNext", equalTo(true));
    }

    @Test
    @DisplayName("현재 꼬리 질문 조회 성공 시 200과 꼬리 질문 정보가 반환된다")
    void currentFollowUpQuestion() {
        Long userId = 1L;
        Long interviewId = 10L;
        Question question = Question.of(6L, interviewId, 1L, "조금 더 자세히 설명해주세요.", QuestionType.FOLLOW_UP, -1);
        NextQuestionResult result = new NextQuestionResult(question, true);

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(questionService.currentQuestion(eq(userId), eq(interviewId))).willReturn(result);

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
        .when()
                .get("/api/interviews/{interviewId}/questions/current", interviewId)
        .then()
                .statusCode(200)
                .body("questionId", equalTo(6))
                .body("question", equalTo("조금 더 자세히 설명해주세요."))
                .body("questionType", equalTo("FOLLOW_UP"))
                .body("hasNext", equalTo(true));
    }

    @Test
    @DisplayName("마지막 질문 조회 시 hasNext가 false이다")
    void currentQuestionLastOne() {
        Long userId = 1L;
        Long interviewId = 10L;
        Question question = Question.of(7L, interviewId, 1L, "마지막 질문입니다.", QuestionType.QUESTION, 4);
        NextQuestionResult result = new NextQuestionResult(question, false);

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(questionService.currentQuestion(eq(userId), eq(interviewId))).willReturn(result);

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
        .when()
                .get("/api/interviews/{interviewId}/questions/current", interviewId)
        .then()
                .statusCode(200)
                .body("hasNext", equalTo(false));
    }

    @Test
    @DisplayName("질문을 찾을 수 없으면 404가 반환된다")
    void currentQuestionNotFound() {
        Long userId = 1L;
        Long interviewId = 10L;

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(questionService.currentQuestion(eq(userId), eq(interviewId)))
                .willThrow(new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
        .when()
                .get("/api/interviews/{interviewId}/questions/current", interviewId)
        .then()
                .statusCode(404)
                .body("code", equalTo("QUESTION_NOT_FOUND"))
                .body("message", equalTo(ErrorCode.QUESTION_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("인증 없이 현재 질문 조회 시 403이 반환된다")
    void currentQuestionWithoutAuth() {
        RestAssuredMockMvc.given()
        .when()
                .get("/api/interviews/{interviewId}/questions/current", 10L)
        .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("종료된 세션에 질문 생성 요청 시 400이 반환된다")
    void createAllQuestionsForCompletedSession() {
        Long userId = 1L;
        Long interviewId = 10L;

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(questionService.createAll(eq(userId), eq(interviewId)))
                .willThrow(new CustomException(ErrorCode.SESSION_ALREADY_COMPLETED));

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
        .when()
                .post("/api/interviews/{interviewId}/questions", interviewId)
        .then()
                .statusCode(400)
                .body("code", equalTo("SESSION_ALREADY_COMPLETED"))
                .body("message", equalTo(ErrorCode.SESSION_ALREADY_COMPLETED.getMessage()));
    }

    @Test
    @DisplayName("종료된 세션에서 현재 질문 조회 시 400이 반환된다")
    void currentQuestionForCompletedSession() {
        Long userId = 1L;
        Long interviewId = 10L;

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(questionService.currentQuestion(eq(userId), eq(interviewId)))
                .willThrow(new CustomException(ErrorCode.SESSION_ALREADY_COMPLETED));

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
        .when()
                .get("/api/interviews/{interviewId}/questions/current", interviewId)
        .then()
                .statusCode(400)
                .body("code", equalTo("SESSION_ALREADY_COMPLETED"))
                .body("message", equalTo(ErrorCode.SESSION_ALREADY_COMPLETED.getMessage()));
    }
}
