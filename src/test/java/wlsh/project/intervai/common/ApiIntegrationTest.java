package wlsh.project.intervai.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import wlsh.project.intervai.answer.integration.AnswerApiSteps;
import wlsh.project.intervai.interview.integration.InterviewApiSteps;
import wlsh.project.intervai.question.integration.QuestionApiSteps;
import wlsh.project.intervai.session.integration.SessionApiSteps;
import wlsh.project.intervai.user.integration.UserApiSteps;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
public abstract class ApiIntegrationTest {

    @LocalServerPort
    protected int port;

    protected HttpClient client;

    @Autowired
    protected ObjectMapper mapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private UserApiSteps userApiSteps;
    private InterviewApiSteps interviewApiSteps;
    private SessionApiSteps sessionApiSteps;
    private QuestionApiSteps questionApiSteps;
    private AnswerApiSteps answerApiSteps;

    @BeforeEach
    void setUpApiIntegrationTest() {
        client = HttpClient.newBuilder()
                .cookieHandler(new CookieManager())
                .build();
        userApiSteps = null;
        interviewApiSteps = null;
        sessionApiSteps = null;
        questionApiSteps = null;
        answerApiSteps = null;
    }

    protected HttpClient newHttpClient() {
        return HttpClient.newBuilder()
                .cookieHandler(new CookieManager())
                .build();
    }

    protected UserApiSteps userSteps() {
        if (userApiSteps == null) {
            userApiSteps = new UserApiSteps(this, client);
        }
        return userApiSteps;
    }

    protected InterviewApiSteps interviewSteps() {
        if (interviewApiSteps == null) {
            interviewApiSteps = new InterviewApiSteps(this, client);
        }
        return interviewApiSteps;
    }

    protected SessionApiSteps sessionSteps() {
        if (sessionApiSteps == null) {
            sessionApiSteps = new SessionApiSteps(this, client);
        }
        return sessionApiSteps;
    }

    protected QuestionApiSteps questionSteps() {
        if (questionApiSteps == null) {
            questionApiSteps = new QuestionApiSteps(this, client);
        }
        return questionApiSteps;
    }

    protected AnswerApiSteps answerSteps() {
        if (answerApiSteps == null) {
            answerApiSteps = new AnswerApiSteps(this, client);
        }
        return answerApiSteps;
    }

    protected String baseUrl() {
        return "http://localhost:" + port;
    }

    public <T> T readBody(HttpResponse<String> response, Class<T> type) throws IOException {
        return mapper.readValue(response.body(), type);
    }

    public HttpResponse<String> sendJson(
            HttpClient client,
            String method,
            String path,
            String accessToken,
            Object body
    ) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + path))
                .header("Accept", MediaType.APPLICATION_JSON_VALUE);

        if (accessToken != null && !accessToken.isBlank()) {
            builder.header("Authorization", "Bearer " + accessToken);
        }

        if (body != null) {
            builder.header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .method(method, HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)));
        } else {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        }

        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    protected void flushRedis() {
        stringRedisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }
}
