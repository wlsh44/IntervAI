package wlsh.project.intervai.feedback.infra;

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
import wlsh.project.intervai.common.entity.BaseEntity;
import wlsh.project.intervai.feedback.domain.Feedback;

@Getter
@Entity
@Table(name = "feedbacks", uniqueConstraints = {
        @jakarta.persistence.UniqueConstraint(name = "uk_feedbacks_answer_id", columnNames = {"answer_id"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedbackEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long answerId;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String feedbackContent;

    @Column(nullable = false)
    private int score;

    private FeedbackEntity(Long answerId, String feedbackContent, int score) {
        this.answerId = answerId;
        this.feedbackContent = feedbackContent;
        this.score = score;
    }

    public static FeedbackEntity from(Feedback feedback) {
        return new FeedbackEntity(feedback.getAnswerId(), feedback.getFeedbackContent(), feedback.getScore());
    }

    public Feedback toDomain() {
        return Feedback.of(id, answerId, feedbackContent, score);
    }
}
