package wlsh.project.intervai.user.domain;

import lombok.Getter;

@Getter
public class User {

    private final Long id;
    private final String nickname;
    private final String passwordHash;

    private User(Long id, String nickname, String passwordHash) {
        this.id = id;
        this.nickname = nickname;
        this.passwordHash = passwordHash;
    }

    public static User create(String nickname, String passwordHash) {
        return new User(null, nickname, passwordHash);
    }

    public static User of(Long id, String nickname, String passwordHash) {
        return new User(id, nickname, passwordHash);
    }
}
