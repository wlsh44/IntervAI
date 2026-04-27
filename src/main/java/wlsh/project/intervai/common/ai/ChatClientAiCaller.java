package wlsh.project.intervai.common.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class ChatClientAiCaller implements AiChatCaller {

    private final ChatClient chatClient;

    public ChatClientAiCaller(@Qualifier("geminiChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public String call(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    @Override
    public String callWithSession(String sessionId, String prompt) {
        return chatClient.prompt()
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, sessionId))
                .user(prompt)
                .call()
                .content();
    }
}
