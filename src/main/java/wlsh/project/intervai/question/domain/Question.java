package wlsh.project.intervai.question.domain;

import lombok.Getter;

@Getter
public class Question {

    private final Long id;
    private final Long interviewId;
    private final Long sessionId;
    private final Long parentQuestionId;
    private final String content;
    private final QuestionType questionType;
    private final int questionIndex;

    private Question(Long id, Long interviewId, Long sessionId,
                     Long parentQuestionId, String content, QuestionType questionType, int questionIndex) {
        this.id = id;
        this.interviewId = interviewId;
        this.sessionId = sessionId;
        this.parentQuestionId = parentQuestionId;
        this.content = content;
        this.questionType = questionType;
        this.questionIndex = questionIndex;
    }

    public static Question createFollowUp(Long interviewId, Long sessionId,
                                          Long parentQuestionId, String content) {
        return new Question(null, interviewId, sessionId, parentQuestionId, content, QuestionType.FOLLOW_UP, -1);
    }

    public static Question create(Long interviewId, Long sessionId,
                                  String content, QuestionType questionType, int questionIndex) {
        return new Question(null, interviewId, sessionId, null, content, questionType, questionIndex);
    }

    public static Question of(Long id, Long interviewId, Long sessionId,
                              Long parentQuestionId, String content, QuestionType questionType, int questionIndex) {
        return new Question(id, interviewId, sessionId, parentQuestionId, content, questionType, questionIndex);
    }

    public static Question of(Long id, Long interviewId, Long sessionId,
                              String content, QuestionType questionType, int questionIndex) {
        return of(id, interviewId, sessionId, null, content, questionType, questionIndex);
    }

    public boolean isFollowUp() {
        return this.questionType.equals(QuestionType.FOLLOW_UP);
    }
}
