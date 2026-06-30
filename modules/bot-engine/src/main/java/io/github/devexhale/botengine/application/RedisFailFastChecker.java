package io.github.devexhale.botengine.application;

import io.github.devexhale.botengine.diagnostics.exception.RedisInitializationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

import static io.github.devexhale.botengine.util.EnvironmentDetector.isTestEnvironment;

/**
 * Performs a fail-fast check to ensure Redis is available and responsive.
 *
 * <p>Verifies the presence of {@code RedisConnectionFactory} and pings the server. Skips the check
 * if the application is running in a test environment.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class RedisFailFastChecker {

  private static final String REDIS_DEPENDENCY_FAIL_MSG =
      "Redis is required but not found in the application context.";

  public static final String REDIS_CONNECTION_FAIL_MSG =
      "Connection to Redis failed. Please check your connection.";

  private static final String REDIS_PONG = "PONG";

  private static final String REDIS_CONNECTION_FACTORY_BEAN_NAME = "redisConnectionFactory";

  private final ApplicationContext context;

  /**
   * Verifies that Redis is available and reachable.
   *
   * @throws RedisInitializationException if Redis is missing or unreachable
   */
  public void check() {
    if (isTestEnvironment()) {
      return;
    }

    if (!context.containsBean(REDIS_CONNECTION_FACTORY_BEAN_NAME)) {
      throw new RedisInitializationException(REDIS_DEPENDENCY_FAIL_MSG);
    }

    RedisConnectionFactory redisConnectionFactory =
        (RedisConnectionFactory) context.getBean(REDIS_CONNECTION_FACTORY_BEAN_NAME);

    try (RedisConnection redisConnection = redisConnectionFactory.getConnection()) {
      String pongResponse = redisConnection.ping();

      if (!REDIS_PONG.equalsIgnoreCase(pongResponse)) {
        throw new RedisInitializationException(REDIS_CONNECTION_FAIL_MSG);
      }
    } catch (RedisInitializationException e) {
      throw e;
    } catch (Exception e) {
      throw new RedisInitializationException(REDIS_CONNECTION_FAIL_MSG, e);
    }
  }
}
