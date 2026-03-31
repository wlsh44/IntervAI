package wlsh.project.intervai.question.domain;

import lombok.Getter;

@Getter
public class Question {

    private final Long id;
    private final Long sessionId;

    private Question(Long id, Long sessionId) {
        this.id = id;
        this.sessionId = sessionId;
    }

    public static Question create(Long sessionId) {
        return new Question(null, sessionId);
    }

    public static Question of(Long id, Long sessionId) {
        return new Question(id, sessionId);
    }
}
