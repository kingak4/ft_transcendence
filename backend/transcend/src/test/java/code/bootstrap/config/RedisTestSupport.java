package code.bootstrap.config;

import java.io.IOException;
import java.net.ServerSocket;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import redis.embedded.RedisServer;

public abstract class RedisTestSupport {

  protected static RedisServer redisServer;
  protected static int redisPort;

  @BeforeAll
  public static void startRedis() throws IOException {
    if (System.getenv("REDIS_HOST") != null) {
      return;
    }
    try (ServerSocket socket = new ServerSocket(0)) {
      redisPort = socket.getLocalPort();
    }
    redisServer = new RedisServer(redisPort);
    redisServer.start();
  }

  @AfterAll
  public static void stopRedis() {
    if (redisServer != null && System.getenv("REDIS_HOST") == null) {
      redisServer.stop();
    }
  }

  @DynamicPropertySource
  public static void redisProperties(DynamicPropertyRegistry registry) {
    String host = System.getenv().getOrDefault("REDIS_HOST", "localhost");
    if (!host.equals("localhost")) {
      int port = Integer.parseInt(System.getenv().getOrDefault("REDIS_PORT", "6379"));
      registry.add("spring.data.redis.host", () -> host);
      registry.add("spring.data.redis.port", () -> port);
      // Also set the old ones just in case
      registry.add("spring.redis.host", () -> host);
      registry.add("spring.redis.port", () -> port);
    } else {
      registry.add("spring.data.redis.host", () -> "localhost");
      registry.add("spring.data.redis.port", () -> redisPort);
      registry.add("spring.redis.host", () -> "localhost");
      registry.add("spring.redis.port", () -> redisPort);
    }
  }
}
