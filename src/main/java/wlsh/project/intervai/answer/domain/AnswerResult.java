package wlsh.project.intervai.answer.domain;

public record AnswerResult(
        Long answerId,
        String feedback,
        String followUpQuestion
) {
    public static AnswerResult of(Long id, String feedback, String followUpQuestion) {
        return new AnswerResult(id, feedback, followUpQuestion);
    }
}
