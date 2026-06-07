package code.shared.config;

import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * @deprecated Prefer {@link EmbeddedRedisTestSupport} (embedded Redis) for tests.
 */
@Deprecated
@TestConfiguration
@Testcontainers
public class RedisTestcontainersConfig {

  public static final int REDIS_PORT = 6379;

  @Container
  @ServiceConnection(name = "redis")
  public static final RedisContainer redisContainer =
      new RedisContainer("redis:latest").withExposedPorts(REDIS_PORT).withReuse(true);
}
