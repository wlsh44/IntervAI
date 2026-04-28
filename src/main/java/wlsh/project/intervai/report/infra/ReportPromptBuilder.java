package wlsh.project.intervai.report.infra;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import wlsh.project.intervai.interview.domain.Interview;

import java.util.Map;

@Slf4j
@Component
public class ReportPromptBuilder {

    private final Resource promptResource;

    public ReportPromptBuilder(@Value("classpath:prompts/summary.st") Resource promptResource) {
        this.promptResource = promptResource;
    }

    public String build(Interview interview, String jobCategory) {
        PromptTemplate template = new PromptTemplate(promptResource);
        String prompt = template.render(Map.of(
                "interviewType", interview.getInterviewType().name(),
                "difficulty", interview.getDifficulty().name(),
                "jobCategory", jobCategory
        ));
        log.debug("[ReportPromptBuilder.build] 생성된 프롬프트:\n{}", prompt);
        return prompt;
    }
}
