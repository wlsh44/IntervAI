package wlsh.project.intervai.common.presentation;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import wlsh.project.intervai.common.AcceptanceTest;

import static org.hamcrest.Matchers.equalTo;

@WebMvcTest(HealthController.class)
class HealthControllerTest extends AcceptanceTest {

    @Test
    @DisplayName("헬스체크 API는 인증 없이 200과 서버 상태를 반환한다")
    void health() {
        RestAssuredMockMvc.given()
        .when()
                .get("/api/health")
        .then()
                .statusCode(200)
                .body("status", equalTo("UP"));
    }
}
