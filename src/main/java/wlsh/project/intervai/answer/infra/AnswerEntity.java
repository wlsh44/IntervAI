package wlsh.project.intervai.answer.infra;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wlsh.project.intervai.answer.domain.Answer;
import wlsh.project.intervai.common.entity.BaseEntity;

@Getter
@Entity
@Table(name = "answers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnswerEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long interviewId;

    @Column(nullable = false)
    private Long sessionId;

    @Column(nullable = false)
    private Long questionId;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private AnswerEntity(Long userId, Long interviewId, Long sessionId, Long questionId, String content) {
        this.userId = userId;
        this.interviewId = interviewId;
        this.sessionId = sessionId;
        this.questionId = questionId;
        this.content = content;
    }

    public static AnswerEntity from(Answer answer) {
        return new AnswerEntity(answer.getUserId(), answer.getInterviewId(),
                answer.getSessionId(), answer.getQuestionId(), answer.getContent());
    }

    public Answer toDomain() {
        return Answer.of(id, userId, interviewId, sessionId, questionId, content);
    }
}
