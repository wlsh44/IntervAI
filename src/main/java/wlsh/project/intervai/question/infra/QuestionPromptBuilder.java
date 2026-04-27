package wlsh.project.intervai.question.infra;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.interview.domain.CsSubject;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.interview.domain.InterviewType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class QuestionPromptBuilder {

    private final Resource promptResource;

    public QuestionPromptBuilder(@Value("classpath:prompts/question-generator.st") Resource promptResource) {
        this.promptResource = promptResource;
    }

    public String build(Interview interview) {
        PromptTemplate template = new PromptTemplate(promptResource);
        return template.render(Map.of(
                "count", interview.getQuestionCount(),
                "interviewType", interview.getInterviewType().getKo(),
                "level", interview.getDifficulty().getKo(),
                "interviewerTone", interview.getInterviewerTone().getKo(),
                "topic", buildTopic(interview)
        ));
    }

    private String buildTopic(Interview interview) {
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
        }

        return topic.toString();
    }

    private String formatCsSubjects(List<CsSubject> csSubjects) {
        return csSubjects.stream()
                .map(s -> s.getCategory().name() + "(" + String.join(", ", s.getTopics()) + ")")
                .collect(Collectors.joining(", "));
    }
}
