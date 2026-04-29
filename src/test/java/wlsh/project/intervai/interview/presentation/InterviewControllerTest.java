package wlsh.project.intervai.interview.presentation;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import wlsh.project.intervai.common.AcceptanceTest;
import wlsh.project.intervai.common.domain.JobCategory;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.interview.application.InterviewService;
import wlsh.project.intervai.interview.domain.CsCategory;
import wlsh.project.intervai.interview.domain.CsSubject;
import wlsh.project.intervai.interview.domain.CreateInterviewCommand;
import wlsh.project.intervai.interview.domain.Difficulty;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.interview.domain.InterviewSummary;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.interview.domain.InterviewerTone;
import wlsh.project.intervai.interview.presentation.dto.CreateInterviewRequest;
import wlsh.project.intervai.interview.presentation.dto.CsSubjectRequest;
import wlsh.project.intervai.session.application.InterviewSessionService;
import wlsh.project.intervai.session.domain.SessionStatus;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

@WebMvcTest(InterviewController.class)
class InterviewControllerTest extends AcceptanceTest {

    @MockitoBean
    private InterviewService interviewService;

    @MockitoBean
    private InterviewSessionService interviewSessionService;

    @Test
    @DisplayName("CS 면접 생성 성공 시 201과 면접 정보가 반환된다")
    void createCsInterview() throws Exception {
        Long userId = 1L;
        List<CsSubject> csSubjects = List.of(
                CsSubject.of(CsCategory.DATA_STRUCTURE, List.of("Map", "List")),
                CsSubject.of(CsCategory.ALGORITHM, List.of("정렬", "dfs/bfs")));
        Interview interview = Interview.of(1L, userId, JobCategory.BACKEND, InterviewType.CS, Difficulty.JUNIOR,
                7, 3, InterviewerTone.FRIENDLY, csSubjects, List.of(), List.of());

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(interviewService.create(eq(userId), any(CreateInterviewCommand.class)))
                .willReturn(interview);

        CreateInterviewRequest request = new CreateInterviewRequest(
                JobCategory.BACKEND, InterviewType.CS, Difficulty.JUNIOR, 7, InterviewerTone.FRIENDLY,
                List.of(
                        new CsSubjectRequest(CsCategory.DATA_STRUCTURE, List.of("Map", "List")),
                        new CsSubjectRequest(CsCategory.ALGORITHM, List.of("정렬", "dfs/bfs"))),
                null,
                null);

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(request))
        .when()
                .post("/api/interviews")
        .then()
                .statusCode(201)
                .body("id", equalTo(1))
                .body("interviewType", equalTo("CS"))
                .body("difficulty", equalTo("JUNIOR"))
                .body("questionCount", equalTo(7))
                .body("interviewerTone", equalTo("FRIENDLY"))
                .body("csSubjects", hasSize(2))
                .body("csSubjects[0].category", equalTo("DATA_STRUCTURE"))
                .body("csSubjects[0].topics[0]", equalTo("Map"))
                .body("csSubjects[1].category", equalTo("ALGORITHM"))
                .body("portfolioLinks", hasSize(0));
    }

    @Test
    @DisplayName("포트폴리오 면접 생성 성공 시 201과 면접 정보가 반환된다")
    void createPortfolioInterview() throws Exception {
        Long userId = 1L;
        List<String> portfolioLinks = List.of("https://github.com/user/project");
        Interview interview = Interview.of(2L, userId, JobCategory.FRONTEND, InterviewType.PORTFOLIO, Difficulty.SENIOR,
                5, 3, InterviewerTone.AGGRESSIVE, List.of(), portfolioLinks, List.of());

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(interviewService.create(eq(userId), any(CreateInterviewCommand.class)))
                .willReturn(interview);

        CreateInterviewRequest request = new CreateInterviewRequest(
                JobCategory.FRONTEND, InterviewType.PORTFOLIO, Difficulty.SENIOR, 5, InterviewerTone.AGGRESSIVE,
                null, List.of("https://github.com/user/project"), null);

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(request))
        .when()
                .post("/api/interviews")
        .then()
                .statusCode(201)
                .body("id", equalTo(2))
                .body("interviewType", equalTo("PORTFOLIO"))
                .body("difficulty", equalTo("SENIOR"))
                .body("questionCount", equalTo(5))
                .body("interviewerTone", equalTo("AGGRESSIVE"))
                .body("csSubjects", hasSize(0))
                .body("portfolioLinks[0]", equalTo("https://github.com/user/project"));
    }

    @Test
    @DisplayName("전체 면접 생성 성공 시 201과 면접 정보가 반환된다")
    void createAllInterview() throws Exception {
        Long userId = 1L;
        List<CsSubject> csSubjects = List.of(CsSubject.of(CsCategory.NETWORK, List.of("http/https")));
        List<String> portfolioLinks = List.of("https://github.com/user/project");
        Interview interview = Interview.of(3L, userId, JobCategory.FULLSTACK, InterviewType.ALL, Difficulty.ENTRY,
                10, 3, InterviewerTone.NORMAL, csSubjects, portfolioLinks, List.of());

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(interviewService.create(eq(userId), any(CreateInterviewCommand.class)))
                .willReturn(interview);

        CreateInterviewRequest request = new CreateInterviewRequest(
                JobCategory.FULLSTACK, InterviewType.ALL, Difficulty.ENTRY, 10, InterviewerTone.NORMAL,
                List.of(new CsSubjectRequest(CsCategory.NETWORK, List.of("http/https"))),
                List.of("https://github.com/user/project"),
                null);

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(request))
        .when()
                .post("/api/interviews")
        .then()
                .statusCode(201)
                .body("id", equalTo(3))
                .body("interviewType", equalTo("ALL"))
                .body("csSubjects", hasSize(1))
                .body("portfolioLinks", hasSize(1));
    }

    @Test
    @DisplayName("면접 유형이 없으면 400이 반환된다")
    void createInterviewWithoutInterviewType() throws Exception {
        given(accessTokenProvider.parseUserId("valid-token")).willReturn(1L);

        String body = """
                {
                    "difficulty": "JUNIOR",
                    "questionCount": 5,
                    "interviewerTone": "FRIENDLY",
                    "csSubjects": [{"category": "DATA_STRUCTURE", "topics": ["Map"]}]
                }
                """;

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
                .contentType(ContentType.JSON)
                .body(body)
        .when()
                .post("/api/interviews")
        .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("숙련도가 없으면 400이 반환된다")
    void createInterviewWithoutDifficulty() throws Exception {
        given(accessTokenProvider.parseUserId("valid-token")).willReturn(1L);

        String body = """
                {
                    "interviewType": "CS",
                    "questionCount": 5,
                    "interviewerTone": "FRIENDLY",
                    "csSubjects": [{"category": "DATA_STRUCTURE", "topics": ["Map"]}]
                }
                """;

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
                .contentType(ContentType.JSON)
                .body(body)
        .when()
                .post("/api/interviews")
        .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("질문 개수가 없으면 400이 반환된다")
    void createInterviewWithoutQuestionCount() throws Exception {
        given(accessTokenProvider.parseUserId("valid-token")).willReturn(1L);

        String body = """
                {
                    "interviewType": "CS",
                    "difficulty": "JUNIOR",
                    "interviewerTone": "FRIENDLY",
                    "csSubjects": [{"category": "DATA_STRUCTURE", "topics": ["Map"]}]
                }
                """;

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
                .contentType(ContentType.JSON)
                .body(body)
        .when()
                .post("/api/interviews")
        .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("면접관 성격이 없으면 400이 반환된다")
    void createInterviewWithoutInterviewerPersonality() throws Exception {
        given(accessTokenProvider.parseUserId("valid-token")).willReturn(1L);

        String body = """
                {
                    "interviewType": "CS",
                    "difficulty": "JUNIOR",
                    "questionCount": 5,
                    "csSubjects": [{"category": "DATA_STRUCTURE", "topics": ["Map"]}]
                }
                """;

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
                .contentType(ContentType.JSON)
                .body(body)
        .when()
                .post("/api/interviews")
        .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("인증 없이 면접 생성 시 403이 반환된다")
    void createInterviewWithoutAuth() throws Exception {
        CreateInterviewRequest request = new CreateInterviewRequest(
                JobCategory.BACKEND, InterviewType.CS, Difficulty.JUNIOR, 5, InterviewerTone.FRIENDLY,
                List.of(new CsSubjectRequest(CsCategory.DATA_STRUCTURE, List.of("Map"))),
                null,
                null);

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(request))
        .when()
                .post("/api/interviews")
        .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("면접 세션 종료 성공 시 200이 반환된다")
    void finishSession() {
        Long userId = 1L;
        Long interviewId = 1L;

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        willDoNothing().given(interviewSessionService).finish(userId, interviewId);

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
        .when()
                .post("/api/interviews/{interviewId}/finish", interviewId)
        .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("인증 없이 면접 세션 종료 시 403이 반환된다")
    void finishSessionWithoutAuth() {
        RestAssuredMockMvc.given()
        .when()
                .post("/api/interviews/1/finish")
        .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("타인의 면접 세션 종료 시도 시 403이 반환된다")
    void finishSessionWhenNotOwner() {
        Long userId = 1L;
        Long interviewId = 1L;

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        willThrow(new CustomException(ErrorCode.INTERVIEW_ACCESS_DENIED))
                .given(interviewSessionService).finish(userId, interviewId);

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
        .when()
                .post("/api/interviews/{interviewId}/finish", interviewId)
        .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("이미 완료된 면접 세션 종료 시도 시 400이 반환된다")
    void finishSessionWhenAlreadyCompleted() {
        Long userId = 1L;
        Long interviewId = 1L;

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        willThrow(new CustomException(ErrorCode.SESSION_ALREADY_COMPLETED))
                .given(interviewSessionService).finish(userId, interviewId);

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
        .when()
                .post("/api/interviews/{interviewId}/finish", interviewId)
        .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("면접 목록 조회 성공 시 200과 면접 목록이 반환된다")
    void getInterviewList() {
        Long userId = 1L;
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 15, 10, 0, 0);
        List<InterviewSummary> summaries = List.of(
                InterviewSummary.of(1L, InterviewType.CS, Difficulty.JUNIOR, 7, SessionStatus.COMPLETED, createdAt),
                InterviewSummary.of(2L, InterviewType.PORTFOLIO, Difficulty.SENIOR, 5, SessionStatus.IN_PROGRESS, createdAt)
        );
        Page<InterviewSummary> page = new PageImpl<>(summaries, PageRequest.of(0, 10), 2);

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(interviewService.getList(eq(userId), any(), any(), any())).willReturn(page);

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
        .when()
                .get("/api/interviews")
        .then()
                .statusCode(200)
                .body("content", hasSize(2))
                .body("content[0].id", equalTo(1))
                .body("content[0].interviewType", equalTo("CS"))
                .body("content[0].difficulty", equalTo("JUNIOR"))
                .body("content[0].questionCount", equalTo(7))
                .body("content[0].sessionStatus", equalTo("COMPLETED"))
                .body("content[1].id", equalTo(2))
                .body("content[1].sessionStatus", equalTo("IN_PROGRESS"))
                .body("totalElements", equalTo(2))
                .body("totalPages", equalTo(1))
                .body("last", equalTo(true));
    }

    @Test
    @DisplayName("interviewType 필터 적용 시 200이 반환된다")
    void getInterviewListWithInterviewTypeFilter() {
        Long userId = 1L;
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 15, 10, 0, 0);
        Page<InterviewSummary> page = new PageImpl<>(
                List.of(InterviewSummary.of(1L, InterviewType.CS, Difficulty.JUNIOR, 7, SessionStatus.COMPLETED, createdAt)),
                PageRequest.of(0, 10), 1);

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(interviewService.getList(eq(userId), eq(InterviewType.CS), any(), any())).willReturn(page);

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
                .queryParam("interviewType", "CS")
        .when()
                .get("/api/interviews")
        .then()
                .statusCode(200)
                .body("content", hasSize(1))
                .body("content[0].interviewType", equalTo("CS"));
    }

    @Test
    @DisplayName("sessionStatus 필터 적용 시 200이 반환된다")
    void getInterviewListWithSessionStatusFilter() {
        Long userId = 1L;
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 15, 10, 0, 0);
        Page<InterviewSummary> page = new PageImpl<>(
                List.of(InterviewSummary.of(1L, InterviewType.CS, Difficulty.JUNIOR, 7, SessionStatus.COMPLETED, createdAt)),
                PageRequest.of(0, 10), 1);

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(interviewService.getList(eq(userId), any(), eq(SessionStatus.COMPLETED), any())).willReturn(page);

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
                .queryParam("sessionStatus", "COMPLETED")
        .when()
                .get("/api/interviews")
        .then()
                .statusCode(200)
                .body("content", hasSize(1))
                .body("content[0].sessionStatus", equalTo("COMPLETED"));
    }

    @Test
    @DisplayName("면접이 없을 때 빈 목록이 반환된다")
    void getInterviewListEmpty() {
        Long userId = 1L;
        Page<InterviewSummary> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(interviewService.getList(eq(userId), any(), any(), any())).willReturn(emptyPage);

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
        .when()
                .get("/api/interviews")
        .then()
                .statusCode(200)
                .body("content", hasSize(0))
                .body("totalElements", equalTo(0))
                .body("totalPages", equalTo(0))
                .body("last", equalTo(true));
    }

    @Test
    @DisplayName("인증 없이 면접 목록 조회 시 403이 반환된다")
    void getInterviewListWithoutAuth() {
        RestAssuredMockMvc.given()
        .when()
                .get("/api/interviews")
        .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("면접 삭제 성공 시 204가 반환된다")
    void deleteInterview() {
        Long userId = 1L;
        Long interviewId = 1L;

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        willDoNothing().given(interviewService).delete(userId, interviewId);

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
        .when()
                .delete("/api/interviews/{interviewId}", interviewId)
        .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("타인 면접 삭제 시도 시 403이 반환된다")
    void deleteInterviewWhenNotOwner() {
        Long userId = 1L;
        Long interviewId = 1L;

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        willThrow(new CustomException(ErrorCode.INTERVIEW_ACCESS_DENIED))
                .given(interviewService).delete(userId, interviewId);

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
        .when()
                .delete("/api/interviews/{interviewId}", interviewId)
        .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("없는 면접 삭제 시도 시 404가 반환된다")
    void deleteInterviewNotFound() {
        Long userId = 1L;
        Long interviewId = 99L;

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        willThrow(new CustomException(ErrorCode.INTERVIEW_NOT_FOUND))
                .given(interviewService).delete(userId, interviewId);

        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer valid-token")
        .when()
                .delete("/api/interviews/{interviewId}", interviewId)
        .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("인증 없이 면접 삭제 시 403이 반환된다")
    void deleteInterviewWithoutAuth() {
        RestAssuredMockMvc.given()
        .when()
                .delete("/api/interviews/1")
        .then()
                .statusCode(403);
    }
}
