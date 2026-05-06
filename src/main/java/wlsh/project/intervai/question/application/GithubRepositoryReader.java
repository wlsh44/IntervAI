package wlsh.project.intervai.question.application;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import wlsh.project.intervai.question.domain.GithubRepositorySummary;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubRepositoryReader {

    private static final String GITHUB_HOST = "github.com";
    private static final String GITHUB_WWW_HOST = "www.github.com";

    private final GithubRepositoryClient githubRepositoryClient;

    public List<GithubRepositorySummary> read(List<String> portfolioLinks) {
        if (CollectionUtils.isEmpty(portfolioLinks)) {
            return List.of();
        }
        return portfolioLinks.stream()
                .filter(this::isGithubLink)
                .map(this::summarizeSafely)
                .toList();
    }

    private GithubRepositorySummary summarizeSafely(String link) {
        try {
            return githubRepositoryClient.summarize(link);
        } catch (RuntimeException e) {
            log.info("[GithubRepositoryReader.summarizeSafely] GitHub 저장소 분석 실패 - url={}, reason={}",
                    link, e.getMessage());
            return GithubRepositorySummary.unavailable(link, e.getMessage());
        }
    }

    private boolean isGithubLink(String link) {
        if (link == null) {
            return false;
        }
        try {
            URI uri = new URI(link.trim());
            String host = uri.getHost();
            long pathSegmentCount = Arrays.stream(uri.getPath().split("/"))
                    .filter(segment -> !segment.isBlank())
                    .count();
            return (GITHUB_HOST.equalsIgnoreCase(host) || GITHUB_WWW_HOST.equalsIgnoreCase(host))
                    && pathSegmentCount >= 2;
        } catch (URISyntaxException | IllegalArgumentException e) {
            return false;
        }
    }
}
