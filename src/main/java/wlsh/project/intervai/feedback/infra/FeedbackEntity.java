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
@Table(name = "feedbacks")
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

    private FeedbackEntity(Long answerId, String feedbackContent) {
        this.answerId = answerId;
        this.feedbackContent = feedbackContent;
    }

    public static FeedbackEntity from(Feedback feedback) {
        return new FeedbackEntity(feedback.getAnswerId(), feedback.getFeedbackContent());
    }

    public static FeedbackEntity create(Long answerId, String feedbackContent) {
        return new FeedbackEntity(answerId, feedbackContent);
    }

    public Feedback toDomain() {
        return Feedback.of(id, answerId, feedbackContent);
    }
}
