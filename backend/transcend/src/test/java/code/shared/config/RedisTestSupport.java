package code.shared.config;

import java.io.IOException;
import java.net.ServerSocket;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import redis.embedded.RedisServer;

public abstract class RedisTestSupport {

  protected static RedisServer redisServer;
  protected static int redisPort;

  @BeforeAll
  public static void startRedis() throws IOException {
    try (ServerSocket socket = new ServerSocket(0)) {
      redisPort = socket.getLocalPort();
    }
    redisServer = new RedisServer(redisPort);
    redisServer.start();
  }

  @AfterAll
  public static void stopRedis() {
    if (redisServer != null) {
      redisServer.stop();
    }
  }
}
