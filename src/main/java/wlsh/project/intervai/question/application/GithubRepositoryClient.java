package wlsh.project.intervai.question.application;

import wlsh.project.intervai.question.domain.GithubRepositorySummary;

public interface GithubRepositoryClient {

    GithubRepositorySummary summarize(String url);
}
