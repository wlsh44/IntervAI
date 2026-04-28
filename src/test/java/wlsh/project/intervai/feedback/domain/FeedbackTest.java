package wlsh.project.intervai.feedback.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wlsh.project.intervai.common.exception.CustomException;
import wlsh.project.intervai.common.exception.ErrorCode;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FeedbackTest {

    @Test
    @DisplayName("피드백 점수가 0보다 작으면 예외가 발생한다")
    void createWhenScoreLessThanZeroThrowsException() {
        assertThatThrownBy(() -> Feedback.create(1L, "피드백", -1))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_FEEDBACK_SCORE.getMessage());
    }

    @Test
    @DisplayName("피드백 점수가 100보다 크면 예외가 발생한다")
    void createWhenScoreGreaterThanOneHundredThrowsException() {
        assertThatThrownBy(() -> Feedback.create(1L, "피드백", 101))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_FEEDBACK_SCORE.getMessage());
    }
}
