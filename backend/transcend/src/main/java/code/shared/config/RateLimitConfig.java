package code.shared.config;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import org.redisson.api.RedissonClient;
import org.redisson.jcache.configuration.RedissonConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfig {

  @Bean
  public CacheManager jCacheManager(RedissonClient redissonClient) {
    CachingProvider provider = Caching.getCachingProvider();
    CacheManager manager = provider.getCacheManager();

    javax.cache.configuration.Configuration<Object, Object> config =
        RedissonConfiguration.fromInstance(redissonClient);

    if (manager.getCache("rate-limit-buckets") == null) {
      manager.createCache("rate-limit-buckets", config);
    }

    return manager;
  }
}
