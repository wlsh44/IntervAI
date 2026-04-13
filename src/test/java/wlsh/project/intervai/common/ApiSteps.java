package wlsh.project.intervai.common;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

public abstract class ApiSteps {

    protected final ApiIntegrationTest api;
    protected final HttpClient client;

    protected ApiSteps(ApiIntegrationTest api, HttpClient client) {
        this.api = api;
        this.client = client;
    }

    protected HttpResponse<String> sendJson(
            String method,
            String path,
            String accessToken,
            Object body
    ) throws IOException, InterruptedException {
        return api.sendJson(client, method, path, accessToken, body);
    }

    protected <T> T readBody(HttpResponse<String> response, Class<T> type) throws IOException {
        return api.readBody(response, type);
    }
}
