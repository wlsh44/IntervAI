package wlsh.project.intervai.question.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import wlsh.project.intervai.question.domain.GithubRepositorySummary;

class GithubRestRepositoryClientTest {

    @Test
    @DisplayName("GitHub access token이 있으면 API 요청에 Bearer 인증 헤더를 포함한다")
    void summarize_withAccessToken_sendsAuthorizationHeader() {
        RestClient.Builder builder = RestClient.builder()
                .defaultHeader("Accept", "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                .defaultHeader("Authorization", "Bearer github-token");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        RestClient restClient = builder.build();
        GithubRestRepositoryClient client = new GithubRestRepositoryClient(restClient);

        server.expect(requestTo("https://api.github.com/repos/user/repo"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer github-token"))
                .andRespond(withSuccess("""
                        {
                          "full_name": "user/repo",
                          "description": "테스트 저장소",
                          "default_branch": "main",
                          "language": "Java"
                        }
                        """, MediaType.APPLICATION_JSON));
        server.expect(requestTo("https://api.github.com/repos/user/repo/languages"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer github-token"))
                .andRespond(withSuccess("""
                        {
                          "Java": 1000,
                          "Shell": 100
                        }
                        """, MediaType.APPLICATION_JSON));
        server.expect(requestTo("https://api.github.com/repos/user/repo/readme"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer github-token"))
                .andRespond(withSuccess("""
                        {
                          "content": "UkVBRE1F"
                        }
                        """, MediaType.APPLICATION_JSON));

        GithubRepositorySummary summary = client.summarize("https://github.com/user/repo");

        assertThat(summary.analysisAvailable()).isTrue();
        assertThat(summary.fullName()).isEqualTo("user/repo");
        assertThat(summary.languages()).containsExactly("Java", "Shell");
        assertThat(summary.readmeSnippet()).isEqualTo("README");
        server.verify();
    }
}
