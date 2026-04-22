package wlsh.project.intervai.feedback.domain;

import lombok.Getter;

@Getter
public class Feedback {

    private final Long id;
    private final Long answerId;
    private final String feedbackContent;
    private final int score;

    private Feedback(Long id, Long answerId, String feedbackContent, int score) {
        this.id = id;
        this.answerId = answerId;
        this.feedbackContent = feedbackContent;
        this.score = score;
    }

    public static Feedback create(Long answerId, String feedbackContent, int score) {
        return new Feedback(null, answerId, feedbackContent, score);
    }

    public static Feedback of(Long id, Long answerId, String feedbackContent, int score) {
        return new Feedback(id, answerId, feedbackContent, score);
    }
}
