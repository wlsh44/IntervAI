package wlsh.project.intervai.answer.application.dto;

public record AnswerResultDto(
        String feedback,
        int score,
        String followUpQuestion
) {
}
