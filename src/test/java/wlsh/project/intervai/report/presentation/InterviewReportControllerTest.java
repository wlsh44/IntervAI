package wlsh.project.intervai.report.presentation;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

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
import wlsh.project.intervai.interview.domain.Difficulty;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.report.application.InterviewReportService;
import wlsh.project.intervai.report.domain.InterviewReport;
import wlsh.project.intervai.report.domain.ReportQuestion;

@WebMvcTest(InterviewReportController.class)
class InterviewReportControllerTest extends AcceptanceTest {

    @MockitoBean
    private InterviewReportService interviewReportService;

    @Test
    @DisplayName("리포트 조회 성공 시 200과 리포트 본문이 반환된다")
    void getReport() {
        Long userId = 1L;
        Long interviewId = 10L;
        InterviewReport report = new InterviewReport(
                3L,
                interviewId,
                InterviewType.CS,
                "BACKEND",
                Difficulty.JUNIOR,
                2,
                LocalDateTime.of(2026, 4, 20, 10, 30),
                88,
                "전반적으로 논리적인 답변 구성이 돋보였습니다.",
                List.of(new ReportQuestion(
                        101L,
                        0,
                        "질문 내용",
                        "답변 내용",
                        "피드백 내용",
                        90,
                        List.of("JPA", "ORM", "ENTITY"),
                        List.of()
                ))
        );

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(interviewReportService.getReport(eq(userId), eq(interviewId))).willReturn(report);

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
                .when()
                .get("/api/interviews/{interviewId}/report", interviewId)
                .then()
                .statusCode(200)
                .body("interviewId", equalTo(10))
                .body("interviewType", equalTo("CS"))
                .body("jobCategory", equalTo("BACKEND"))
                .body("difficulty", equalTo("JUNIOR"))
                .body("questionCount", equalTo(2))
                .body("totalScore", equalTo(88))
                .body("overallComment", equalTo("전반적으로 논리적인 답변 구성이 돋보였습니다."))
                .body("questions[0].questionId", equalTo(101))
                .body("questions[0].score", equalTo(90))
                .body("questions[0].keywords[0]", equalTo("JPA"));
    }

    @Test
    @DisplayName("리포트가 없으면 404가 반환된다")
    void getReport_notFound() {
        Long userId = 1L;
        Long interviewId = 10L;

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(interviewReportService.getReport(eq(userId), eq(interviewId)))
                .willThrow(new CustomException(ErrorCode.REPORT_NOT_FOUND));

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
                .when()
                .get("/api/interviews/{interviewId}/report", interviewId)
                .then()
                .statusCode(404)
                .body("code", equalTo("REPORT_NOT_FOUND"))
                .body("message", equalTo(ErrorCode.REPORT_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("세션이 아직 종료되지 않았으면 400이 반환된다")
    void getReport_sessionNotCompleted() {
        Long userId = 1L;
        Long interviewId = 10L;

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(interviewReportService.getReport(eq(userId), eq(interviewId)))
                .willThrow(new CustomException(ErrorCode.SESSION_NOT_COMPLETED));

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
                .when()
                .get("/api/interviews/{interviewId}/report", interviewId)
                .then()
                .statusCode(400)
                .body("code", equalTo("SESSION_NOT_COMPLETED"))
                .body("message", equalTo(ErrorCode.SESSION_NOT_COMPLETED.getMessage()));
    }

    @Test
    @DisplayName("타인 면접 리포트 조회 시 403이 반환된다")
    void getReport_accessDenied() {
        Long userId = 1L;
        Long interviewId = 10L;

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(interviewReportService.getReport(eq(userId), eq(interviewId)))
                .willThrow(new CustomException(ErrorCode.INTERVIEW_ACCESS_DENIED));

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
                .when()
                .get("/api/interviews/{interviewId}/report", interviewId)
                .then()
                .statusCode(403)
                .body("code", equalTo("INTERVIEW_ACCESS_DENIED"))
                .body("message", equalTo(ErrorCode.INTERVIEW_ACCESS_DENIED.getMessage()));
    }

    @Test
    @DisplayName("인증 없이 리포트 조회 시 403이 반환된다")
    void getReport_withoutAuth() {
        RestAssuredMockMvc.given()
                .when()
                .get("/api/interviews/{interviewId}/report", 10L)
                .then()
                .statusCode(403);
    }
}
