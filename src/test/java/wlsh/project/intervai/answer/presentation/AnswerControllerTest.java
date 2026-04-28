package wlsh.project.intervai.answer.presentation;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import wlsh.project.intervai.answer.application.AnswerService;
import wlsh.project.intervai.answer.domain.AnswerResult;
import wlsh.project.intervai.answer.domain.CreateAnswerCommand;
import wlsh.project.intervai.answer.presentation.dto.CreateAnswerRequest;
import wlsh.project.intervai.common.AcceptanceTest;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@WebMvcTest(AnswerController.class)
class AnswerControllerTest extends AcceptanceTest {

    @MockitoBean
    private AnswerService answerService;

    @Test
    @DisplayName("답변 제출 성공 시 201과 피드백이 반환된다")
    void answer() throws Exception {
        Long userId = 1L;
        Long interviewId = 10L;
        AnswerResult result = AnswerResult.of("좋은 답변입니다. 핵심을 잘 짚었습니다.", 85);

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(answerService.answer(eq(userId), eq(interviewId), eq(1L), any(CreateAnswerCommand.class)))
                .willReturn(result);

        CreateAnswerRequest request = new CreateAnswerRequest(1L, "운영체제는 하드웨어를 관리하는 소프트웨어입니다.");

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(request))
                .when()
                .post("/api/interviews/{interviewId}/answers", interviewId)
                .then()
                .statusCode(201)
                .body("feedback", equalTo("좋은 답변입니다. 핵심을 잘 짚었습니다."))
                .body("score", equalTo(85));
    }

    @Test
    @DisplayName("이미 답변한 질문에 다시 답변하면 409가 반환된다")
    void answerAlreadyExists() throws Exception {
        Long userId = 1L;
        Long interviewId = 10L;

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(answerService.answer(eq(userId), eq(interviewId), eq(1L), any(CreateAnswerCommand.class)))
                .willThrow(new CustomException(ErrorCode.ANSWER_ALREADY_EXISTS));

        CreateAnswerRequest request = new CreateAnswerRequest(1L, "중복 답변입니다.");

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(request))
                .when()
                .post("/api/interviews/{interviewId}/answers", interviewId)
                .then()
                .statusCode(409)
                .body("code", equalTo("ANSWER_ALREADY_EXISTS"))
                .body("message", equalTo(ErrorCode.ANSWER_ALREADY_EXISTS.getMessage()));
    }

    @Test
    @DisplayName("종료된 세션에 답변하면 400이 반환된다")
    void answerToCompletedSession() throws Exception {
        Long userId = 1L;
        Long interviewId = 10L;

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(answerService.answer(eq(userId), eq(interviewId), eq(1L), any(CreateAnswerCommand.class)))
                .willThrow(new CustomException(ErrorCode.SESSION_ALREADY_COMPLETED));

        CreateAnswerRequest request = new CreateAnswerRequest(1L, "답변 내용");

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(request))
                .when()
                .post("/api/interviews/{interviewId}/answers", interviewId)
                .then()
                .statusCode(400)
                .body("code", equalTo("SESSION_ALREADY_COMPLETED"))
                .body("message", equalTo(ErrorCode.SESSION_ALREADY_COMPLETED.getMessage()));
    }

    @Test
    @DisplayName("질문 ID가 없으면 400이 반환된다")
    void answerWithoutQuestionId() throws Exception {
        given(accessTokenProvider.parseUserId("valid-token")).willReturn(1L);

        String body = "{\"content\": \"답변 내용\"}";

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/interviews/{interviewId}/answers", 10L)
                .then()
                .statusCode(400);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    @DisplayName("답변 내용이 비어있으면 400이 반환된다")
    void answerWithBlankContent(String content) throws Exception {
        given(accessTokenProvider.parseUserId("valid-token")).willReturn(1L);

        String body = content == null
                ? "{\"questionId\": 1}"
                : mapper.writeValueAsString(new CreateAnswerRequest(1L, content));

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/interviews/{interviewId}/answers", 10L)
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("인증 없이 답변 제출 시 403이 반환된다")
    void answerWithoutAuth() throws Exception {
        CreateAnswerRequest request = new CreateAnswerRequest(1L, "답변 내용");

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(request))
                .when()
                .post("/api/interviews/{interviewId}/answers", 10L)
                .then()
                .statusCode(403);
    }
}
