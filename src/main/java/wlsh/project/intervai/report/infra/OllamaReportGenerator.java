package wlsh.project.intervai.report.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.report.application.ReportGenerator;
import wlsh.project.intervai.report.application.dto.ReportGenerationResultDto;

import java.util.Map;

@Component
@Profile("prod")
public class OllamaReportGenerator implements ReportGenerator {

    private final ChatClient chatClient;
    private final Resource promptResource;
    private final ObjectMapper objectMapper;

    public OllamaReportGenerator(@Qualifier("ollamaChatClient") ChatClient chatClient,
                                 @Value("classpath:prompts/summary.st") Resource promptResource,
                                 ObjectMapper objectMapper) {
        this.chatClient = chatClient;
        this.promptResource = promptResource;
        this.objectMapper = objectMapper;
    }

    @Override
    public ReportGenerationResultDto generate(String sessionId, Interview interview, String jobCategory) {
        PromptTemplate template = new PromptTemplate(promptResource);
        String userPrompt = template.render(Map.of(
                "interviewType", interview.getInterviewType().name(),
                "difficulty", interview.getDifficulty().name(),
                "jobCategory", jobCategory
        ));

        String response = chatClient.prompt()
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, sessionId))
                .user(userPrompt)
                .call()
                .content();

        return parseResult(response);
    }

    private ReportGenerationResultDto parseResult(String response) {
        try {
            return objectMapper.readValue(response.trim(), ReportGenerationResultDto.class);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
