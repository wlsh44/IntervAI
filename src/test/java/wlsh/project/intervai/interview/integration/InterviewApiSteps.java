package wlsh.project.intervai.interview.integration;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import wlsh.project.intervai.common.ApiIntegrationTest;
import wlsh.project.intervai.common.ApiSteps;
import wlsh.project.intervai.interview.domain.CsCategory;
import wlsh.project.intervai.interview.domain.Difficulty;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.interview.domain.InterviewerTone;
import wlsh.project.intervai.interview.presentation.dto.CreateInterviewRequest;
import wlsh.project.intervai.interview.presentation.dto.CreateInterviewResponse;
import wlsh.project.intervai.interview.presentation.dto.CsSubjectRequest;

import static org.assertj.core.api.Assertions.assertThat;

public class InterviewApiSteps extends ApiSteps {

    public InterviewApiSteps(ApiIntegrationTest api, HttpClient client) {
        super(api, client);
    }

    public CreateInterviewResponse createInterview(String accessToken) throws IOException, InterruptedException {
        CreateInterviewRequest request = new CreateInterviewRequest(
                InterviewType.CS,
                Difficulty.JUNIOR,
                5,
                InterviewerTone.NORMAL,
                List.of(
                        new CsSubjectRequest(CsCategory.NETWORK, List.of("HTTP", "TCP/IP")),
                        new CsSubjectRequest(CsCategory.DATABASE, List.of("INDEX", "TRANSACTION"))
                ),
                null,
                null
        );

        HttpResponse<String> response = sendJson(
                "POST",
                "/api/interviews",
                accessToken,
                request
        );

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        return readBody(response, CreateInterviewResponse.class);
    }
}
