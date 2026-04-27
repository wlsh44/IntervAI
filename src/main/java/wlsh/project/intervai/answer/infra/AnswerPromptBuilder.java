package wlsh.project.intervai.answer.infra;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.answer.domain.Answer;
import wlsh.project.intervai.question.domain.Question;

import java.util.Map;

@Component
public class AnswerPromptBuilder {

    private final Resource promptResource;

    public AnswerPromptBuilder(@Value("classpath:prompts/feedback-followup.st") Resource promptResource) {
        this.promptResource = promptResource;
    }

    public String build(Question question, Answer answer) {
        PromptTemplate template = new PromptTemplate(promptResource);
        return template.render(Map.of(
                "question", question.getContent(),
                "answerText", answer.getContent()
        ));
    }
}
