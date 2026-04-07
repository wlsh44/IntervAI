package wlsh.project.intervai.question.infra;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.interview.domain.CsSubject;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.question.application.QuestionGenerator;

@Component
@Profile("prod")
public class OllamaQuestionGenerator implements QuestionGenerator {

    private final ChatClient chatClient;
    private final Resource promptResource;
    private final ObjectMapper objectMapper;

    public OllamaQuestionGenerator(@Qualifier("ollamaChatClient") ChatClient chatClient,
                                   @Value("classpath:prompts/question-generator.st") Resource promptResource,
                                   ObjectMapper objectMapper) {
        this.chatClient = chatClient;
        this.promptResource = promptResource;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<String> generateAll(Interview interview) {
        PromptTemplate template = new PromptTemplate(promptResource);
        String userPrompt = template.render(Map.of(
                "count", interview.getQuestionCount(),
                "interviewType", interview.getInterviewType().getKo(),
                "level", interview.getDifficulty().getKo(),
                "interviewerTone", interview.getInterviewerTone().getKo(),
                "topic", buildTopic(interview)
        ));

        String response = chatClient.prompt()
                .user(userPrompt)
                .call()
                .content();

        return parseQuestions(response);
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

    private List<String> parseQuestions(String response) {
        try {
            return objectMapper.readValue(response.trim(), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            return List.of(response.trim());
        }
    }
}
