package wlsh.project.intervai.feedback.domain;

import lombok.Getter;

@Getter
public class Feedback {

    private final Long id;
    private final Long answerId;
    private final String feedbackContent;

    private Feedback(Long id, Long answerId, String feedbackContent) {
        this.id = id;
        this.answerId = answerId;
        this.feedbackContent = feedbackContent;
    }

    public static Feedback create(Long userId, String feedbackContent) {
        return new Feedback(null, userId, feedbackContent);
    }

    public static Feedback of(Long id, Long answerId, String feedbackContent) {
        return new Feedback(id, answerId, feedbackContent);
    }
}
