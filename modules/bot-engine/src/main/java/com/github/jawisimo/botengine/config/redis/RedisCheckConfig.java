package com.github.jawisimo.botengine.config.redis;

import com.github.jawisimo.botengine.config.BotProperties;
import com.github.jawisimo.botengine.exception.RedisConnectionException;
import com.github.jawisimo.botengine.util.EnvironmentDetector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import static com.github.jawisimo.botengine.util.EnvironmentDetector.isTestEnvironment;

/**
 * Performs startup verification of Redis integration when persistent user state is enabled.
 *
 * <p>Validates that Redis dependency is present in the application context, a connection factory is
 * configured, and the Redis server is reachable. Executes a ping check to ensure the connection is
 * operational.
 *
 * <p>Fails fast with {@link RedisConnectionException} if any verification step fails. The
 * verification is skipped in test environments to avoid build failures when Redis is not available
 * during testing.
 *
 * @since 1.0
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class RedisCheckConfig {

  private static final String REDIS_DEPENDENCY_FAIL_MSG =
      "Redis is required but not found in the application context. "
          + "Please add spring-boot-starter-data-redis "
          + "and configure spring.data.redis.*properties.";

  private static final String REDIS_CONNECTION_FAIL_MSG =
      "Connection to Redis failed. Please check your connection.";

  private static final String REDIS_PONG = "PONG";

  private static final String REDIS_CONNECTION_FACTORY_BEAN_NAME = "redisConnectionFactory";

  private final BotProperties properties;
  private final ApplicationContext context;

  /**
   * Creates an {@link ApplicationRunner} that performs Redis dependency and connectivity checks
   * during application startup. The check is skipped in test environments.
   *
   * @return the Redis verification runner
   */
  @Bean
  public ApplicationRunner redisCheckRunner() {
    return args -> {
      if (!properties.userStatePersistent() || isTestEnvironment()) {
        return;
      }

      if (!context.containsBean(REDIS_CONNECTION_FACTORY_BEAN_NAME)) {
        log.error(REDIS_DEPENDENCY_FAIL_MSG);
        throw new RedisConnectionException(REDIS_DEPENDENCY_FAIL_MSG);
      }

      RedisConnectionFactory redisConnectionFactory =
          (RedisConnectionFactory) context.getBean(REDIS_CONNECTION_FACTORY_BEAN_NAME);

      try (RedisConnection redisConnection = redisConnectionFactory.getConnection()) {
        String pongResponse = redisConnection.ping();

        if (!REDIS_PONG.equalsIgnoreCase(pongResponse)) {
          log.error(REDIS_CONNECTION_FAIL_MSG);
          throw new RedisConnectionException(REDIS_CONNECTION_FAIL_MSG);
        }
      } catch (RedisConnectionException e) {
        throw e;
      } catch (Exception e) {
        log.error(REDIS_CONNECTION_FAIL_MSG, e);
        throw new RedisConnectionException(REDIS_CONNECTION_FAIL_MSG, e);
      }
    };
  }
}
