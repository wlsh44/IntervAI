package wlsh.project.intervai.user.integration;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import org.springframework.http.HttpStatus;
import wlsh.project.intervai.common.ApiIntegrationTest;
import wlsh.project.intervai.common.ApiSteps;
import wlsh.project.intervai.user.presentation.dto.CreateUserRequest;
import wlsh.project.intervai.user.presentation.dto.CreateUserResponse;
import wlsh.project.intervai.user.presentation.dto.LoginRequest;
import wlsh.project.intervai.user.presentation.dto.LoginResponse;

import static org.assertj.core.api.Assertions.assertThat;

public class UserApiSteps extends ApiSteps {

    public UserApiSteps(ApiIntegrationTest api, HttpClient client) {
        super(api, client);
    }

    public CreateUserResponse signUp(String nickname, String password) throws IOException, InterruptedException {
        HttpResponse<String> response = sendJson(
                "POST",
                "/api/users/sign-up",
                null,
                new CreateUserRequest(nickname, password)
        );

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        return readBody(response, CreateUserResponse.class);
    }

    public LoginResponse login(String nickname, String password) throws IOException, InterruptedException {
        HttpResponse<String> response = sendJson(
                "POST",
                "/api/users/login",
                null,
                new LoginRequest(nickname, password)
        );

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        return readBody(response, LoginResponse.class);
    }
}
