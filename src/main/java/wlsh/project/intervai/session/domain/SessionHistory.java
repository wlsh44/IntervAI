package wlsh.project.intervai.session.domain;

import jakarta.annotation.Nullable;
import wlsh.project.intervai.feedback.infra.FeedbackEntity;
import wlsh.project.intervai.question.domain.QuestionType;
import wlsh.project.intervai.session.application.dto.SessionHistoryDto;

public record SessionHistory(
        Long questionId,
        Long answerId,
        String questionContent,
        String answerContent,
        String feedbackContent,
        QuestionType getQuestionType,
        Integer questionIndex
) {

    public static SessionHistory createSessionHistory(SessionHistoryDto sessionHistoryDto, @Nullable FeedbackEntity feedback) {
        return new SessionHistory(
                sessionHistoryDto.getQuestionId(),
                sessionHistoryDto.getAnswerId(),
                sessionHistoryDto.getQuestionContent(),
                sessionHistoryDto.getAnswerContent(),
                feedback != null ? feedback.getFeedbackContent() : null,
                sessionHistoryDto.getQuestionType(),
                sessionHistoryDto.getQuestionIndex()
        );
    }
}
