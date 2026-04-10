package wlsh.project.intervai.interview.presentation.dto;

import wlsh.project.intervai.interview.domain.NextQuestionResult;
import wlsh.project.intervai.question.domain.QuestionType;

public record NextQuestionResponse(
        Long questionId,
        String question,
        QuestionType questionType,
        boolean hasNext
) {
    public static NextQuestionResponse of(NextQuestionResult result) {
        return new NextQuestionResponse(
                result.question().getId(),
                result.question().getContent(),
                result.question().getQuestionType(),
                result.hasNext()
        );
    }
}
