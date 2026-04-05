package wlsh.project.intervai.interview.presentation.dto;

import wlsh.project.intervai.interview.domain.NextQuestionResult;

public record NextQuestionResponse(
        Long questionId,
        String question,
        boolean hasNext
) {
    public static NextQuestionResponse of(NextQuestionResult result) {
        return new NextQuestionResponse(
                result.question().getId(),
                result.question().getContent(),
                result.hasNext()
        );
    }
}
