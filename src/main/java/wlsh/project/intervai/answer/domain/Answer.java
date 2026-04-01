package wlsh.project.intervai.answer.domain;

import lombok.Getter;

@Getter
public class Answer {

    private final Long id;
    private final Long userId;
    private final Long interviewId;
    private final Long sessionId;
    private final Long questionId;
    private final String content;

    private Answer(Long id, Long userId, Long interviewId, Long sessionId, Long questionId, String content) {
        this.id = id;
        this.userId = userId;
        this.interviewId = interviewId;
        this.sessionId = sessionId;
        this.questionId = questionId;
        this.content = content;
    }

    public static Answer create(Long userId, Long interviewId, Long sessionId, Long questionId, String content) {
        return new Answer(null, userId, interviewId, sessionId, questionId, content);
    }

    public static Answer of(Long id, Long userId, Long interviewId, Long sessionId, Long questionId, String content) {
        return new Answer(id, userId, interviewId, sessionId, questionId, content);
    }
}
