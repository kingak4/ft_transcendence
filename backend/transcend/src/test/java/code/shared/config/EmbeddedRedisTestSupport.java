package code.shared.config;

import java.io.IOException;
import java.net.ServerSocket;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import redis.embedded.RedisServer;

public abstract class EmbeddedRedisTestSupport {

  private static final RedisServer REDIS_SERVER;
  private static final int REDIS_PORT;

  static {
    try (ServerSocket socket = new ServerSocket(0)) {
      REDIS_PORT = socket.getLocalPort();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    try {
      REDIS_SERVER = new RedisServer(REDIS_PORT);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    REDIS_SERVER.start();

    System.setProperty("spring.data.redis.port", String.valueOf(REDIS_PORT));
    System.setProperty("spring.data.redis.host", "localhost");

    Runtime.getRuntime().addShutdownHook(new Thread(REDIS_SERVER::stop));
  }

  @Autowired private RedisConnectionFactory connectionFactory;

  @BeforeEach
  void cleanRedis() {
    connectionFactory.getConnection().serverCommands().flushAll();
  }
}
