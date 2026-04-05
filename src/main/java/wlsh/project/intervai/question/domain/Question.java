package wlsh.project.intervai.question.domain;

import lombok.Getter;

@Getter
public class Question {

    private final Long id;
    private final Long interviewId;
    private final Long sessionId;
    private final String content;
    private final QuestionType type;
    private final int questionIndex;

    private Question(Long id, Long interviewId, Long sessionId,
                     String content, QuestionType type, int questionIndex) {
        this.id = id;
        this.interviewId = interviewId;
        this.sessionId = sessionId;
        this.content = content;
        this.type = type;
        this.questionIndex = questionIndex;
    }

    public static Question create(Long interviewId, Long sessionId,
                                   String content, QuestionType questionType, int questionIndex) {
        return new Question(null, interviewId, sessionId, content, questionType, questionIndex);
    }

    public static Question of(Long id, Long interviewId, Long sessionId,
                                String content, QuestionType type, int questionIndex) {
        return new Question(id, interviewId, sessionId, content, type, questionIndex);
    }
}
