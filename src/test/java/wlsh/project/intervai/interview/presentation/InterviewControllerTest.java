package wlsh.project.intervai.interview.presentation;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import wlsh.project.intervai.common.AcceptanceTest;
import wlsh.project.intervai.interview.application.InterviewService;
import wlsh.project.intervai.interview.domain.CsCategory;
import wlsh.project.intervai.interview.domain.CsSubject;
import wlsh.project.intervai.interview.domain.CreateInterviewCommand;
import wlsh.project.intervai.interview.domain.Difficulty;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.interview.domain.InterviewerTone;
import wlsh.project.intervai.interview.presentation.dto.CreateInterviewRequest;
import wlsh.project.intervai.interview.presentation.dto.CsSubjectRequest;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@WebMvcTest(InterviewController.class)
class InterviewControllerTest extends AcceptanceTest {

    @MockitoBean
    private InterviewService interviewService;

    @Test
    @DisplayName("CS 면접 생성 성공 시 201과 면접 정보가 반환된다")
    void createCsInterview() throws Exception {
        Long userId = 1L;
        List<CsSubject> csSubjects = List.of(
                CsSubject.of(CsCategory.DATA_STRUCTURE, List.of("Map", "List")),
                CsSubject.of(CsCategory.ALGORITHM, List.of("정렬", "dfs/bfs")));
        Interview interview = Interview.of(1L, userId, InterviewType.CS, Difficulty.JUNIOR,
                7, InterviewerTone.FRIENDLY, csSubjects, List.of());

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(interviewService.create(eq(userId), any(CreateInterviewCommand.class)))
                .willReturn(interview);

        CreateInterviewRequest request = new CreateInterviewRequest(
                InterviewType.CS, Difficulty.JUNIOR, 7, InterviewerTone.FRIENDLY,
                List.of(
                        new CsSubjectRequest(CsCategory.DATA_STRUCTURE, List.of("Map", "List")),
                        new CsSubjectRequest(CsCategory.ALGORITHM, List.of("정렬", "dfs/bfs"))),
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
        Interview interview = Interview.of(2L, userId, InterviewType.PORTFOLIO, Difficulty.SENIOR,
                5, InterviewerTone.AGGRESSIVE, List.of(), portfolioLinks);

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(interviewService.create(eq(userId), any(CreateInterviewCommand.class)))
                .willReturn(interview);

        CreateInterviewRequest request = new CreateInterviewRequest(
                InterviewType.PORTFOLIO, Difficulty.SENIOR, 5, InterviewerTone.AGGRESSIVE,
                null, List.of("https://github.com/user/project"));

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
        Interview interview = Interview.of(3L, userId, InterviewType.ALL, Difficulty.ENTRY,
                10, InterviewerTone.NORMAL, csSubjects, portfolioLinks);

        given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId);
        given(interviewService.create(eq(userId), any(CreateInterviewCommand.class)))
                .willReturn(interview);

        CreateInterviewRequest request = new CreateInterviewRequest(
                InterviewType.ALL, Difficulty.ENTRY, 10, InterviewerTone.NORMAL,
                List.of(new CsSubjectRequest(CsCategory.NETWORK, List.of("http/https"))),
                List.of("https://github.com/user/project"));

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
                InterviewType.CS, Difficulty.JUNIOR, 5, InterviewerTone.FRIENDLY,
                List.of(new CsSubjectRequest(CsCategory.DATA_STRUCTURE, List.of("Map"))),
                null);

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(request))
        .when()
                .post("/api/interviews")
        .then()
                .statusCode(403);
    }
}
