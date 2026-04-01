package wlsh.project.intervai.question.domain;

import lombok.Getter;

@Getter
public class Question {

    private final Long id;
    private final Long userId;
    private final Long interviewId;
    private final Long sessionId;
    private final String content;
    private final QuestionType type;

    private Question(Long id, Long userId, Long interviewId, Long sessionId, String content, QuestionType type) {
        this.id = id;
        this.userId = userId;
        this.interviewId = interviewId;
        this.sessionId = sessionId;
        this.content = content;
        this.type = type;
    }

    public static Question create(Long userId, Long interviewId, Long sessionId, String content) {
        return new Question(null, userId, interviewId, sessionId, content, QuestionType.QUESTION);
    }

    public static Question of(Long id, Long userId, Long interviewId, Long sessionId, String content, QuestionType type) {
        return new Question(id, userId, interviewId, sessionId, content, type);
    }
}
