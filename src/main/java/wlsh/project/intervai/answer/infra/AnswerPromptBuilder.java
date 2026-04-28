package wlsh.project.intervai.answer.infra;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import wlsh.project.intervai.answer.domain.Answer;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.question.domain.Question;

import java.util.Map;

@Slf4j
@Component
public class AnswerPromptBuilder {

    private final Resource promptResource;

    public AnswerPromptBuilder(@Value("classpath:prompts/feedback-followup.st") Resource promptResource) {
        this.promptResource = promptResource;
    }

    public String build(Question question, Answer answer, Interview interview) {
        PromptTemplate template = new PromptTemplate(promptResource);
        String prompt = template.render(Map.of(
                "question", question.getContent(),
                "answerText", answer.getContent(),
                "difficulty", interview.getDifficulty().getKo(),
                "jobCategory", interview.getJobCategory().name()
        ));
        log.debug("[AnswerPromptBuilder.build] 생성된 프롬프트:\n{}", prompt);
        return prompt;
    }
}
