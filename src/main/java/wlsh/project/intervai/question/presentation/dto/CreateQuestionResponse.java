package wlsh.project.intervai.question.presentation.dto;

import wlsh.project.intervai.question.domain.Question;

public record CreateQuestionResponse(
        Long id,
        Long userId,
        Long interviewId,
        Long sessionId,
        String content
) {
    public static CreateQuestionResponse of(Question question) {
        return new CreateQuestionResponse(
                question.getId(),
                question.getUserId(),
                question.getInterviewId(),
                question.getSessionId(),
                question.getContent()
        );
    }
}
