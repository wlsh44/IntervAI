package wlsh.project.intervai.feedback.domain;

import lombok.Getter;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;

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
        validateScore(score);
        return new Feedback(null, answerId, feedbackContent, score);
    }

    public static Feedback of(Long id, Long answerId, String feedbackContent, int score) {
        return new Feedback(id, answerId, feedbackContent, score);
    }

    private static void validateScore(int score) {
        if (score < 0 || score > 100) {
            throw new CustomException(ErrorCode.INVALID_FEEDBACK_SCORE);
        }
    }
}
