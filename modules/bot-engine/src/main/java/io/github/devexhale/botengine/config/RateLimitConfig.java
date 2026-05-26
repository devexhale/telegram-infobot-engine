package io.github.devexhale.botengine.config;

import static io.github.devexhale.botengine.application.RedisFailFastChecker.REDIS_CONNECTION_FAIL_MSG;

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce;
import io.github.devexhale.botengine.diagnostics.exception.RedisInitializationException;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(name = "org.springframework.data.redis.connection.RedisConnectionFactory")
public class RateLimitConfig {

  private static final Duration BUCKET_TTL = Duration.ofSeconds(10);

  @Value("${spring.data.redis.host}")
  private String redisHost;

  @Value("${spring.data.redis.port}")
  private int redisPort;

  @Value("${spring.data.redis.password:}")
  private String redisPassword;

  @Bean(destroyMethod = "shutdown")
  public RedisClient redisClient() {
    RedisURI.Builder builder = RedisURI.builder().withHost(redisHost).withPort(redisPort);

    if (redisPassword != null && !redisPassword.isBlank()) {
      builder.withPassword(redisPassword.toCharArray());
    }

    return RedisClient.create(builder.build());
  }

  @Bean(destroyMethod = "close")
  public StatefulRedisConnection<String, byte[]> bucket4jConnection(RedisClient redisClient) {
    try {
      return redisClient.connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));
    } catch (Exception e) {
      throw new RedisInitializationException(REDIS_CONNECTION_FAIL_MSG, e);
    }
  }

  @Bean
  public ProxyManager<String> bucketProxyManager(
      StatefulRedisConnection<String, byte[]> bucket4jConnection) {
    return Bucket4jLettuce.casBasedBuilder(bucket4jConnection)
        .expirationAfterWrite(
            ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(BUCKET_TTL))
        .build();
  }
}
