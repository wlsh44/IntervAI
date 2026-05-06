package wlsh.project.intervai.question.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wlsh.project.intervai.question.domain.GithubRepositorySummary;

@ExtendWith(MockitoExtension.class)
class GithubRepositoryReaderTest {

    @Mock
    private GithubRepositoryClient githubRepositoryClient;

    @Test
    @DisplayName("GitHub 저장소 URL만 분석 대상으로 전달한다")
    void read_filtersGithubRepositoryUrl() {
        GithubRepositoryReader reader = new GithubRepositoryReader(githubRepositoryClient);
        GithubRepositorySummary summary = GithubRepositorySummary.available(
                "https://github.com/user/repo",
                "user/repo",
                "description",
                "main",
                "Java",
                List.of("Java"),
                "README");
        given(githubRepositoryClient.summarize("https://github.com/user/repo")).willReturn(summary);
        given(githubRepositoryClient.summarize("https://www.github.com/user/repo2")).willReturn(summary);

        List<GithubRepositorySummary> summaries = reader.read(List.of(
                "https://example.com/?next=github.com/user/repo",
                "https://github.com/user/repo",
                "https://github.com/user",
                "https://www.github.com/user/repo2"));

        assertThat(summaries).hasSize(2);
    }

    @Test
    @DisplayName("단일 저장소 분석 실패는 unavailable 요약으로 변환한다")
    void read_returnsUnavailableSummaryWhenClientThrows() {
        GithubRepositoryReader reader = new GithubRepositoryReader(githubRepositoryClient);
        given(githubRepositoryClient.summarize("https://github.com/user/repo"))
                .willThrow(new IllegalStateException("rate limit"));

        List<GithubRepositorySummary> summaries = reader.read(List.of("https://github.com/user/repo"));

        assertThat(summaries).hasSize(1);
        assertThat(summaries.getFirst().analysisAvailable()).isFalse();
        assertThat(summaries.getFirst().failureReason()).isEqualTo("rate limit");
    }
}
