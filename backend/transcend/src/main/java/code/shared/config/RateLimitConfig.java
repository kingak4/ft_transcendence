package code.shared.config;

import com.giffing.bucket4j.spring.boot.starter.config.cache.ProxyManagerWrapper;
import com.giffing.bucket4j.spring.boot.starter.config.cache.SyncCacheResolver;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitConfig {

  @Value("${spring.data.redis.host}")
  private String redisHost;

  @Value("${spring.data.redis.port}")
  private int redisPort;

  @Bean(destroyMethod = "shutdown")
  public RedisClient redisClient() {
    return RedisClient.create(String.format("redis://%s:%d", redisHost, redisPort));
  }

  @Bean
  public ProxyManager<String> proxyManager(RedisClient redisClient) {
    StatefulRedisConnection<String, byte[]> connection = redisClient
        .connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));

    return Bucket4jLettuce.casBasedBuilder(connection)
        .expirationAfterWrite(ExpirationAfterWriteStrategy.fixedTimeToLive(Duration.ofHours(1)))
        .build();
  }
}