package wlsh.project.intervai.user.presentation.dto;

import wlsh.project.intervai.user.domain.User;

public record UserMeResponse(
        Long id,
        String name
) {

    public static UserMeResponse of(User user) {
        return new UserMeResponse(user.getId(), user.getNickname());
    }
}
