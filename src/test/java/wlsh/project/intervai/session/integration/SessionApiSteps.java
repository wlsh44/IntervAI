package wlsh.project.intervai.session.integration;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import org.springframework.http.HttpStatus;
import wlsh.project.intervai.common.ApiIntegrationTest;
import wlsh.project.intervai.common.ApiSteps;
import wlsh.project.intervai.interview.presentation.dto.CreateSessionResponse;

import static org.assertj.core.api.Assertions.assertThat;

public class SessionApiSteps extends ApiSteps {

    public SessionApiSteps(ApiIntegrationTest api, HttpClient client) {
        super(api, client);
    }

    public CreateSessionResponse createSession(String accessToken, Long interviewId)
            throws IOException, InterruptedException {
        HttpResponse<String> response = sendJson(
                "POST",
                "/api/interviews/" + interviewId + "/sessions",
                accessToken,
                null
        );

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        return readBody(response, CreateSessionResponse.class);
    }

    public void finishSession(String accessToken, Long interviewId) throws IOException, InterruptedException {
        HttpResponse<String> response = sendJson(
                "POST",
                "/api/interviews/" + interviewId + "/sessions/finish",
                accessToken,
                null
        );

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body()).isBlank();
    }
}
