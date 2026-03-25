package wlsh.project.intervai.user.presentation.dto;

import wlsh.project.intervai.user.domain.User;

public record LoginResponse(
        Long id,
        String nickname,
        String accessToken
) {

    public static LoginResponse of(User user, String accessToken) {
        return new LoginResponse(user.getId(), user.getNickname(), accessToken);
    }
}
