package wlsh.project.intervai.common.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("prod")
public class ChatClientAiCaller implements AiChatCaller {

    private final ChatClient chatClient;

    public ChatClientAiCaller(@Qualifier("geminiChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public String call(String prompt) {
        log.debug("[ChatClientAiCaller.call] 프롬프트 전송 - promptLength={}",
                prompt == null ? 0 : prompt.length());
        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();
        log.debug("[ChatClientAiCaller.call] 응답 수신 - responseLength={}",
                response == null ? 0 : response.length());
        return response;
    }

    @Override
    public String callWithSession(String sessionId, String prompt) {
        log.debug("[ChatClientAiCaller.callWithSession] sessionId={}, 프롬프트 전송 - promptLength={}",
                sessionId, prompt == null ? 0 : prompt.length());
        String response = chatClient.prompt()
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, sessionId))
                .user(prompt)
                .call()
                .content();
        log.debug("[ChatClientAiCaller.callWithSession] sessionId={}, 응답 수신 - responseLength={}",
                sessionId, response == null ? 0 : response.length());
        return response;
    }
}
