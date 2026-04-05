package wlsh.project.intervai.common.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient ollamaChatClient() {
        OllamaApi ollamaApi = OllamaApi.builder()
                .baseUrl("http://localhost:11434")
                .build();
        OllamaChatModel builder = OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(
                        OllamaChatOptions.builder()
                                .model("mistral")
                                .temperature(0.7)
                                .build()
                )
                .build();
        return ChatClient.builder(builder)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(
                                        MessageWindowChatMemory.builder()
                                                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                                                .build())
                                .build())
                .build();
    }
}
