package wlsh.project.intervai.answer.integration;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import org.springframework.http.HttpStatus;
import wlsh.project.intervai.answer.presentation.dto.CreateAnswerRequest;
import wlsh.project.intervai.answer.presentation.dto.CreateAnswerResponse;
import wlsh.project.intervai.common.ApiIntegrationTest;
import wlsh.project.intervai.common.ApiSteps;

import static org.assertj.core.api.Assertions.assertThat;

public class AnswerApiSteps extends ApiSteps {

    public AnswerApiSteps(ApiIntegrationTest api, HttpClient client) {
        super(api, client);
    }

    public CreateAnswerResponse scenario07AnswerCurrentQuestion(
            String accessToken,
            Long interviewId,
            Long questionId,
            String content
    ) throws IOException, InterruptedException {
        HttpResponse<String> response = sendJson(
                "POST",
                "/api/interviews/" + interviewId + "/answers",
                accessToken,
                new CreateAnswerRequest(questionId, content)
        );

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        return readBody(response, CreateAnswerResponse.class);
    }
}
