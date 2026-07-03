package io.github.devexhale.botengine.config;

import static io.github.devexhale.botengine.application.RedisFailFastChecker.REDIS_CONNECTION_FAIL_MSG;

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce;
import io.github.devexhale.botengine.diagnostics.exception.RedisInitializationException;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * Configures Redis infrastructure for distributed rate limiting using Bucket4J.
 *
 * @since 1.0
 */
@Configuration
@DependsOn("startUpApplicationRunner")
public class RateLimitConfig {

  private static final Duration BUCKET_TTL = Duration.ofSeconds(10);

  /**
   * Establishes a stateful Redis connection for Bucket4J state storage.
   *
   * @param connectionFactory the Redis connection factory to extract the native client from
   * @return the stateful Redis connection
   * @throws RedisInitializationException if the connection fails
   */
  @Bean(destroyMethod = "close")
  @Lazy
  public StatefulRedisConnection<String, byte[]> botEngineBucket4jConnection(
      RedisConnectionFactory connectionFactory) {
    if (!(connectionFactory instanceof LettuceConnectionFactory lettuceFactory)) {
      throw new IllegalStateException(
          "Bucket4J requires a Lettuce-based RedisConnectionFactory. Found: "
              + connectionFactory.getClass().getName());
    }

    RedisClient redisClient = (RedisClient) lettuceFactory.getNativeClient();

    if (redisClient == null) {
      throw new IllegalStateException("LettuceConnectionFactory has not been initialized yet.");
    }

    try {
      return redisClient.connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));
    } catch (Exception e) {
      throw new RedisInitializationException(REDIS_CONNECTION_FAIL_MSG, e);
    }
  }

  /**
   * Creates a Redis-backed Bucket4J {@link ProxyManager} for distributed rate limiting.
   *
   * @param bucket4jConnection the stateful Redis connection
   * @return the configured proxy manager
   */
  @Bean
  @Lazy
  public ProxyManager<String> botEngineBucketProxyManager(
      StatefulRedisConnection<String, byte[]> bucket4jConnection) {
    return Bucket4jLettuce.casBasedBuilder(bucket4jConnection)
        .expirationAfterWrite(
            ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(BUCKET_TTL))
        .build();
  }
}
