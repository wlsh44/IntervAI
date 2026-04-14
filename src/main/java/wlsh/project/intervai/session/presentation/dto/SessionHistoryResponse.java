package wlsh.project.intervai.session.presentation.dto;

import wlsh.project.intervai.question.domain.QuestionType;
import wlsh.project.intervai.session.domain.SessionHistory;

import java.util.List;

public record SessionHistoryResponse(
        Long questionId,
        Long answerId,
        String questionContent,
        String answerContent,
        String feedbackContent,
        QuestionType questionType,
        Integer questionIndex
) {
    public static SessionHistoryResponse from(SessionHistory history) {
        return new SessionHistoryResponse(
                history.questionId(),
                history.answerId(),
                history.questionContent(),
                history.answerContent(),
                history.feedbackContent(),
                history.getQuestionType(),
                history.questionIndex()
        );
    }

    public static List<SessionHistoryResponse> from(List<SessionHistory> histories) {
        return histories.stream().map(SessionHistoryResponse::from).toList();
    }
}
