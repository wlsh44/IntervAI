package wlsh.project.intervai.common.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class ChatClientConfig {

    @Bean
    @Profile("prod")
    public ChatClient geminiChatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultAdvisors(chatMemoryAdvisor())
                .build();
    }

    @Bean
    @Profile("local-ollama")
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
                .defaultAdvisors(chatMemoryAdvisor())
                .build();
    }

    private MessageChatMemoryAdvisor chatMemoryAdvisor() {
        return MessageChatMemoryAdvisor.builder(
                        MessageWindowChatMemory.builder()
                                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                                .build())
                .build();
    }
}
