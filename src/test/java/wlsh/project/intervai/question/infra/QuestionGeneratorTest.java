package wlsh.project.intervai.question.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.core.io.ClassPathResource;
import wlsh.project.intervai.common.ai.AiChatCaller;
import wlsh.project.intervai.common.ai.ChatClientAiCaller;
import wlsh.project.intervai.common.domain.JobCategory;
import wlsh.project.intervai.interview.domain.CreateInterviewCommand;
import wlsh.project.intervai.interview.domain.CsCategory;
import wlsh.project.intervai.interview.domain.CsSubject;
import wlsh.project.intervai.interview.domain.Difficulty;
import wlsh.project.intervai.interview.domain.Interview;
import wlsh.project.intervai.interview.domain.InterviewType;
import wlsh.project.intervai.interview.domain.InterviewerTone;

import java.util.List;

class QuestionGeneratorTest {

    static ApiQuestionGenerator generator;

    static {
        OllamaApi ollamaApi = OllamaApi.builder()
                .baseUrl("http://localhost:11434")
                .build();
        OllamaChatModel chatModel = OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(
                        OllamaChatOptions.builder()
                                .model("mistral")
                                .temperature(0.7)
                                .build()
                )
                .build();
        ChatClient chatClient = ChatClient.builder(chatModel).build();
        AiChatCaller aiChatCaller = new ChatClientAiCaller(chatClient);
        QuestionPromptBuilder promptBuilder = new QuestionPromptBuilder(
                new ClassPathResource("prompts/question-generator.st"));
        generator = new ApiQuestionGenerator(aiChatCaller, promptBuilder, new ObjectMapper());
    }

    @Test
    @Disabled
    void prompt() {
        Interview interview = Interview.create(1L, new CreateInterviewCommand(
                JobCategory.BACKEND,
                InterviewType.CS,
                Difficulty.ENTRY,
                5,
                InterviewerTone.NORMAL,
                List.of(CsSubject.of(CsCategory.NETWORK, List.of("전체"))),
                null,
                null));

        List<String> questions = generator.generateAll(interview);

        System.out.println("questions = " + questions);
    }
}
