package wlsh.project.intervai.question.integration;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import org.springframework.http.HttpStatus;
import wlsh.project.intervai.common.ApiIntegrationTest;
import wlsh.project.intervai.common.ApiSteps;
import wlsh.project.intervai.interview.presentation.dto.CreateQuestionsResponse;
import wlsh.project.intervai.interview.presentation.dto.NextQuestionResponse;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionApiSteps extends ApiSteps {

    public QuestionApiSteps(ApiIntegrationTest api, HttpClient client) {
        super(api, client);
    }

    public CreateQuestionsResponse createQuestions(String accessToken, Long interviewId)
            throws IOException, InterruptedException {
        HttpResponse<String> response = sendJson(
                "POST",
                "/api/interviews/" + interviewId + "/questions",
                accessToken,
                null
        );

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        return readBody(response, CreateQuestionsResponse.class);
    }

    public NextQuestionResponse getCurrentQuestion(String accessToken, Long interviewId)
            throws IOException, InterruptedException {
        HttpResponse<String> response = sendJson(
                "GET",
                "/api/interviews/" + interviewId + "/questions/current",
                accessToken,
                null
        );

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        return readBody(response, NextQuestionResponse.class);
    }
}
