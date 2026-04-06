package wlsh.project.intervai.answer.domain;

public record AnswerResult(
        String feedback
) {
    public static AnswerResult of(String feedback) {
        return new AnswerResult(feedback);
    }
}
