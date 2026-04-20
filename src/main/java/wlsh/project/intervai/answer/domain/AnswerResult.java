package wlsh.project.intervai.answer.domain;

public record AnswerResult(
        String feedback,
        int score
) {
    public static AnswerResult of(String feedback, int score) {
        return new AnswerResult(feedback, score);
    }
}
