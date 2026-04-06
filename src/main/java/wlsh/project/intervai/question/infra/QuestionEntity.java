package wlsh.project.intervai.question.infra;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wlsh.project.intervai.common.entity.BaseEntity;
import wlsh.project.intervai.question.domain.Question;
import wlsh.project.intervai.question.domain.QuestionType;

@Getter
@Entity
@Table(name = "questions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long interviewId;

    @Column(nullable = false)
    private Long sessionId;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

    @Column(nullable = false)
    private int questionIndex;

    private QuestionEntity(Long interviewId, Long sessionId,
                           String content, QuestionType questionType, int questionIndex) {
        this.interviewId = interviewId;
        this.sessionId = sessionId;
        this.content = content;
        this.questionType = questionType;
        this.questionIndex = questionIndex;
    }

    public static QuestionEntity from(Question question) {
        return new QuestionEntity(question.getInterviewId(),
                question.getSessionId(), question.getContent(), question.getQuestionType(),
                question.getQuestionIndex());
    }

    public static QuestionEntity createFollowUp(Long interviewId, Long sessionId,
                                                String content) {
        return new QuestionEntity(interviewId, sessionId, content, QuestionType.FOLLOW_UP, -1);
    }

    public Question toDomain() {
        return Question.of(id, interviewId, sessionId, content, questionType, questionIndex);
    }
}
