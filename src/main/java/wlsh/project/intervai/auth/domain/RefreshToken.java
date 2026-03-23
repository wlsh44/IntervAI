package wlsh.project.intervai.auth.domain;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class RefreshToken {

    private final Long id;
    private final Long userId;
    private final String token;
    private final LocalDateTime expiresAt;

    private RefreshToken(Long id, Long userId, String token, LocalDateTime expiresAt) {
        this.id = id;
        this.userId = userId;
        this.token = token;
        this.expiresAt = expiresAt;
    }

    public static RefreshToken create(Long userId, String token, LocalDateTime expiresAt) {
        return new RefreshToken(null, userId, token, expiresAt);
    }

    public static RefreshToken of(Long id, Long userId, String token, LocalDateTime expiresAt) {
        return new RefreshToken(id, userId, token, expiresAt);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
