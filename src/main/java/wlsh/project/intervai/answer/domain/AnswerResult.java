package wlsh.project.intervai.answer.domain;

public record AnswerResult(
        Long answerId,
        String feedback,
        Long followUpQuestionId,
        String followUpQuestion
) {
    public static AnswerResult of(Long id, String feedback, Long followUpQuestionId, String followUpQuestion) {
        return new AnswerResult(id, feedback, followUpQuestionId, followUpQuestion);
    }
}
