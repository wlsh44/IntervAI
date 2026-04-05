package wlsh.project.intervai.answer.domain;

public record CreateAnswerCommand(
        String content,
        boolean isFollowUP
) {
}
