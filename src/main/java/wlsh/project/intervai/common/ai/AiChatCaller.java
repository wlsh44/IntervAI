package wlsh.project.intervai.common.ai;

public interface AiChatCaller {

    String call(String prompt);

    String callWithSession(String sessionId, String prompt);
}
