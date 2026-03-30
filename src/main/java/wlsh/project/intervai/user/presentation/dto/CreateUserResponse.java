package wlsh.project.intervai.user.presentation.dto;

import wlsh.project.intervai.user.domain.User;

public record CreateUserResponse(
        Long id,
        String nickname,
        String accessToken
) {

    public static CreateUserResponse of(User user, String accessToken) {
        return new CreateUserResponse(user.getId(), user.getNickname(), accessToken);
    }
}
