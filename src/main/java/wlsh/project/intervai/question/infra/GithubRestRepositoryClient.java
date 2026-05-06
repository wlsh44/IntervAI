package wlsh.project.intervai.question.infra;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import wlsh.project.intervai.question.application.GithubRepositoryClient;
import wlsh.project.intervai.question.domain.GithubRepositorySummary;

@Slf4j
@Component
public class GithubRestRepositoryClient implements GithubRepositoryClient {

    private static final String GITHUB_HOST = "github.com";
    private static final int README_SNIPPET_LIMIT = 2_000;
    private static final int LANGUAGE_LIMIT = 5;
    private static final String BEARER_PREFIX = "Bearer ";
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(3);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(5);

    private final RestClient restClient;

    @Autowired
    public GithubRestRepositoryClient(RestClient.Builder restClientBuilder,
                                      @Value("${github.access-token:}") String githubAccessToken) {
        RestClient.Builder builder = restClientBuilder
                .requestFactory(requestFactory())
                .defaultHeader("Accept", "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28");
        if (StringUtils.hasText(githubAccessToken)) {
            builder.defaultHeader("Authorization", BEARER_PREFIX + githubAccessToken);
        }
        this.restClient = builder.build();
    }

    GithubRestRepositoryClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public GithubRepositorySummary summarize(String url) {
        try {
            RepositoryPath path = parse(url);
            Map<String, Object> repository = getMap(
                    "https://api.github.com/repos/{owner}/{repo}", path.owner(), path.repo());
            Map<String, Object> languages = getMap(
                    "https://api.github.com/repos/{owner}/{repo}/languages", path.owner(), path.repo());
            String readmeSnippet = fetchReadmeSnippet(path);

            return GithubRepositorySummary.available(
                    url,
                    value(repository, "full_name"),
                    value(repository, "description"),
                    value(repository, "default_branch"),
                    value(repository, "language"),
                    formatLanguages(languages),
                    readmeSnippet
            );
        } catch (IllegalArgumentException | RestClientException e) {
            log.info("[GithubRestRepositoryClient.summarize] GitHub 저장소 분석 실패 - url={}, reason={}",
                    url, e.getMessage());
            return GithubRepositorySummary.unavailable(url, e.getMessage());
        }
    }

    private RepositoryPath parse(String url) {
        URI uri = URI.create(url.trim());
        if (!GITHUB_HOST.equalsIgnoreCase(uri.getHost())) {
            throw new IllegalArgumentException("GitHub 링크가 아닙니다.");
        }

        String[] paths = uri.getPath().split("/");
        if (paths.length < 3 || paths[1].isBlank() || paths[2].isBlank()) {
            throw new IllegalArgumentException("GitHub 저장소 경로를 찾을 수 없습니다.");
        }

        return new RepositoryPath(paths[1], paths[2].replaceAll("\\.git$", ""));
    }

    private SimpleClientHttpRequestFactory requestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECT_TIMEOUT);
        requestFactory.setReadTimeout(READ_TIMEOUT);
        return requestFactory;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getMap(String uri, String owner, String repo) {
        Map<String, Object> response = restClient.get()
                .uri(uri, owner, repo)
                .retrieve()
                .body(Map.class);
        if (response == null) {
            return Map.of();
        }
        return response;
    }

    private String fetchReadmeSnippet(RepositoryPath path) {
        Map<String, Object> readme;
        try {
            readme = getMap("https://api.github.com/repos/{owner}/{repo}/readme", path.owner(), path.repo());
        } catch (RestClientException e) {
            log.debug("[GithubRestRepositoryClient.fetchReadmeSnippet] README 조회 실패 - owner={}, repo={}",
                    path.owner(), path.repo());
            return null;
        }
        String content = value(readme, "content");
        if (content == null || content.isBlank()) {
            return null;
        }

        byte[] decoded = Base64.getMimeDecoder().decode(content);
        String normalized = new String(decoded, StandardCharsets.UTF_8)
                .replaceAll("\\s+", " ")
                .trim();
        if (normalized.length() <= README_SNIPPET_LIMIT) {
            return normalized;
        }
        return normalized.substring(0, README_SNIPPET_LIMIT);
    }

    private List<String> formatLanguages(Map<String, Object> languages) {
        return languages.entrySet().stream()
                .sorted((left, right) -> Long.compare(asLong(right.getValue()), asLong(left.getValue())))
                .limit(LANGUAGE_LIMIT)
                .map(Map.Entry::getKey)
                .toList();
    }

    private long asLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        return 0L;
    }

    private String value(Map<String, Object> source, String key) {
        Object value = source.get(key);
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    private record RepositoryPath(String owner, String repo) {
    }
}
