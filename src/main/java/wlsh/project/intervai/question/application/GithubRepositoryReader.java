package wlsh.project.intervai.question.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import wlsh.project.intervai.question.domain.GithubRepositorySummary;

@Component
@RequiredArgsConstructor
public class GithubRepositoryReader {

    private final GithubRepositoryClient githubRepositoryClient;

    public List<GithubRepositorySummary> read(List<String> portfolioLinks) {
        if (CollectionUtils.isEmpty(portfolioLinks)) {
            return List.of();
        }
        return portfolioLinks.stream()
                .filter(this::isGithubLink)
                .map(githubRepositoryClient::summarize)
                .toList();
    }

    private boolean isGithubLink(String link) {
        return link != null && link.contains("github.com/");
    }
}
