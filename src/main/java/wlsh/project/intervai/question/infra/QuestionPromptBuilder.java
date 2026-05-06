package wlsh.project.intervai.question.infra;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import wlsh.project.intervai.interview.domain.CsSubject;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.question.domain.GithubRepositorySummary;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class QuestionPromptBuilder {

    private final Resource promptResource;

    public QuestionPromptBuilder(@Value("classpath:prompts/question-generator.st") Resource promptResource) {
        this.promptResource = promptResource;
    }

    public String build(Interview interview) {
        return build(interview, List.of());
    }

    public String build(Interview interview, List<GithubRepositorySummary> githubRepositorySummaries) {
        PromptTemplate template = new PromptTemplate(promptResource);
        String prompt = template.render(Map.of(
                "count", interview.getQuestionCount(),
                "interviewType", interview.getInterviewType().getKo(),
                "level", interview.getDifficulty().getKo(),
                "interviewerTone", interview.getInterviewerTone().getKo(),
                "topic", buildTopic(interview, githubRepositorySummaries)
        ));
        log.debug("[QuestionPromptBuilder.build] 생성된 프롬프트:\n{}", prompt);
        return prompt;
    }

    private String buildTopic(Interview interview, List<GithubRepositorySummary> githubRepositorySummaries) {
        InterviewType type = interview.getInterviewType();
        StringBuilder topic = new StringBuilder();

        if (type == InterviewType.CS || type == InterviewType.ALL) {
            topic.append("CS 분야: ").append(formatCsSubjects(interview.getCsSubjects()));
        }

        if (type == InterviewType.PORTFOLIO || type == InterviewType.ALL) {
            if (!topic.isEmpty()) {
                topic.append("\n");
            }
            topic.append("포트폴리오 링크:\n");
            for (String link : interview.getPortfolioLinks()) {
                topic.append("- ").append(link).append("\n");
            }
            appendGithubRepositorySummaries(topic, githubRepositorySummaries);
        }

        return topic.toString();
    }

    private void appendGithubRepositorySummaries(StringBuilder topic,
                                                 List<GithubRepositorySummary> githubRepositorySummaries) {
        if (githubRepositorySummaries == null || githubRepositorySummaries.isEmpty()) {
            return;
        }
        topic.append("\nGitHub 저장소 분석:\n");
        for (GithubRepositorySummary summary : githubRepositorySummaries) {
            topic.append(summary.toPromptText());
        }
    }

    private String formatCsSubjects(List<CsSubject> csSubjects) {
        return csSubjects.stream()
                .map(s -> s.getCategory().name() + "(" + String.join(", ", s.getTopics()) + ")")
                .collect(Collectors.joining(", "));
    }
}
