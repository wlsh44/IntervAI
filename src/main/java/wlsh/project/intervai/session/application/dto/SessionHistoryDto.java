package wlsh.project.intervai.session.application.dto;

import wlsh.project.intervai.question.domain.QuestionType;

public interface SessionHistoryDto {
    Long getQuestionId();
    Long getParentQuestionId();
    Long getAnswerId();
    QuestionType getQuestionType();
    Integer getQuestionIndex();
    String getQuestionContent();
    String getAnswerContent();
}
