package wlsh.project.intervai.common.config;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicBoolean;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

@Configuration
@Profile("local")
public class EmbeddedRedisConfig {

    private static final Object LOCK = new Object();
    private static final AtomicBoolean STARTED = new AtomicBoolean(false);
    private static final int PORT = findAvailablePort();
    private static RedisServer redisServer;

    static {
        System.setProperty("spring.data.redis.port", String.valueOf(PORT));
    }

    @PostConstruct
    public void start() throws IOException {
        synchronized (LOCK) {
            if (STARTED.get()) {
                return;
            }

            redisServer = new RedisServer(PORT);
            redisServer.start();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                synchronized (LOCK) {
                    if (redisServer != null) {
                        try {
                            redisServer.stop();
                        } catch (IOException ignored) {
                        }
                    }
                }
            }));
            STARTED.set(true);
        }

    }

    private static int findAvailablePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to allocate Redis port for tests", e);
        }
    }
}
