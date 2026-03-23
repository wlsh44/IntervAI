package wlsh.project.intervai.auth.infra;

import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRedisRepository {

    // refresh-token::{userId}
    private static final String KEY_PREFIX = "refresh-token::%s";

    private final StringRedisTemplate stringRedisTemplate;

    public void save(Long userId, String token, Duration ttl) {
        stringRedisTemplate.opsForValue().set(generateKey(userId), token, ttl);
    }

    private String generateKey(Long userId) {
        return KEY_PREFIX.formatted(String.valueOf(userId));
    }

    public Optional<String> findTokenByUserId(Long userId) {
        return Optional.ofNullable(stringRedisTemplate.opsForValue().get(generateKey(userId)));
    }

    public void deleteByToken(String token) {
        stringRedisTemplate.delete(KEY_PREFIX + token);
    }
}
