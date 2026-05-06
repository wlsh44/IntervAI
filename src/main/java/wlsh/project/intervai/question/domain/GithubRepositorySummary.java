package wlsh.project.intervai.question.domain;

import java.util.List;

public record GithubRepositorySummary(
        String url,
        String fullName,
        String description,
        String defaultBranch,
        String primaryLanguage,
        List<String> languages,
        String readmeSnippet,
        boolean analysisAvailable,
        String failureReason
) {

    public static GithubRepositorySummary available(String url, String fullName, String description,
                                                    String defaultBranch, String primaryLanguage,
                                                    List<String> languages, String readmeSnippet) {
        return new GithubRepositorySummary(url, fullName, description, defaultBranch, primaryLanguage,
                languages == null ? List.of() : List.copyOf(languages), readmeSnippet, true, null);
    }

    public static GithubRepositorySummary unavailable(String url, String failureReason) {
        return new GithubRepositorySummary(url, null, null, null, null, List.of(), null, false, failureReason);
    }

    public String toPromptText() {
        if (!analysisAvailable) {
            return "- 링크: " + url + "\n"
                    + "  - 분석 상태: 실패 (" + failureReason + ")\n";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("- 저장소: ").append(fullName).append("\n");
        builder.append("  - 링크: ").append(url).append("\n");
        appendIfPresent(builder, "설명", description);
        appendIfPresent(builder, "기본 브랜치", defaultBranch);
        appendIfPresent(builder, "주요 언어", primaryLanguage);
        if (!languages.isEmpty()) {
            builder.append("  - 언어 구성: ").append(String.join(", ", languages)).append("\n");
        }
        appendIfPresent(builder, "README 요약 원문", readmeSnippet);
        return builder.toString();
    }

    private void appendIfPresent(StringBuilder builder, String label, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        builder.append("  - ").append(label).append(": ").append(value).append("\n");
    }
}
