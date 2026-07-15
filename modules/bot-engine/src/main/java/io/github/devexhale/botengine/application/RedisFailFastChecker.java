package io.github.devexhale.botengine.application;

import io.github.devexhale.botengine.diagnostics.exception.RedisInitializationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

/**
 * Performs a fail-fast connectivity check to ensure Redis is available and responsive during
 * application startup.
 *
 * @since 1.0
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RedisFailFastChecker {

  public static final String CONNECTION_FAIL_MSG =
      "Connection to Redis failed. Please check your connection.";
  private static final String CHECK_START_LOG = "Checking Redis connectivity...";
  private static final String CHECK_PASS_LOG = "Redis connectivity check passed in {} ms.";
  private static final String CHECK_FAIL_LOG = "Redis connectivity check failed after {} ms.";
  private static final String PONG = "PONG";
  private static final String REDIS_CONNECTION_FACTORY_BEAN_NAME = "redisConnectionFactory";

  private final ApplicationContext context;

  /**
   * Attempts to establish a connection to Redis and sends a PING command to verify responsiveness.
   *
   * @throws RedisInitializationException if Redis is unreachable or does not respond with PONG
   */
  public void check() {
    log.info(CHECK_START_LOG);

    long start = System.currentTimeMillis();

    RedisConnectionFactory redisConnectionFactory =
        (RedisConnectionFactory) context.getBean(REDIS_CONNECTION_FACTORY_BEAN_NAME);

    try (RedisConnection redisConnection = redisConnectionFactory.getConnection()) {
      String pongResponse = redisConnection.ping();

      if (PONG.equalsIgnoreCase(pongResponse)) {
        log.info(CHECK_PASS_LOG, System.currentTimeMillis() - start);
        return;
      }

      throw new RedisInitializationException(CONNECTION_FAIL_MSG);
    } catch (RedisInitializationException e) {
      log.error(CHECK_FAIL_LOG, System.currentTimeMillis() - start, e);
      throw e;
    } catch (Exception e) {
      log.error(CHECK_FAIL_LOG, System.currentTimeMillis() - start, e);
      throw new RedisInitializationException(CONNECTION_FAIL_MSG, e);
    }
  }
}
