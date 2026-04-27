package wlsh.project.intervai.report.infra;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.interview.domain.Interview;

import java.util.Map;

@Component
public class ReportPromptBuilder {

    private final Resource promptResource;

    public ReportPromptBuilder(@Value("classpath:prompts/summary.st") Resource promptResource) {
        this.promptResource = promptResource;
    }

    public String build(Interview interview, String jobCategory) {
        PromptTemplate template = new PromptTemplate(promptResource);
        return template.render(Map.of(
                "interviewType", interview.getInterviewType().name(),
                "difficulty", interview.getDifficulty().name(),
                "jobCategory", jobCategory
        ));
    }
}
