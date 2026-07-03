package io.github.devexhale.botengine.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import io.github.devexhale.botengine.diagnostics.exception.RedisInitializationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@ExtendWith(MockitoExtension.class)
class RedisFailFastCheckerTest {

  private static final String REDIS_CONNECTION_FACTORY_BEAN_NAME = "redisConnectionFactory";

  @Mock private ApplicationContext context;

  @Mock private RedisConnectionFactory redisConnectionFactory;

  @Mock private RedisConnection redisConnection;

  @InjectMocks private RedisFailFastChecker redisFailFastChecker;

  @Test
  void check_shouldPassSuccessfully_whenPingReturnsPong() {
    when(context.getBean(REDIS_CONNECTION_FACTORY_BEAN_NAME)).thenReturn(redisConnectionFactory);
    when(redisConnectionFactory.getConnection()).thenReturn(redisConnection);
    when(redisConnection.ping()).thenReturn("PONG");

    assertDoesNotThrow(() -> redisFailFastChecker.check());
  }

  @Test
  void check_shouldThrowException_whenPingReturnsNotPong() {
    when(context.getBean(REDIS_CONNECTION_FACTORY_BEAN_NAME)).thenReturn(redisConnectionFactory);
    when(redisConnectionFactory.getConnection()).thenReturn(redisConnection);
    when(redisConnection.ping()).thenReturn("NOT_PONG");

    RedisInitializationException exception =
        assertThrows(RedisInitializationException.class, () -> redisFailFastChecker.check());

    assertEquals(RedisFailFastChecker.REDIS_CONNECTION_FAIL_MSG, exception.getMessage());
  }

  @Test
  void check_shouldThrowWrappedException_whenConnectionFails() {
    String connectionTimeoutMsg = "Connection timeout";

    when(context.getBean(REDIS_CONNECTION_FACTORY_BEAN_NAME)).thenReturn(redisConnectionFactory);
    when(redisConnectionFactory.getConnection())
        .thenThrow(new RuntimeException(connectionTimeoutMsg));

    RedisInitializationException exception =
        assertThrows(RedisInitializationException.class, () -> redisFailFastChecker.check());

    assertEquals(RedisFailFastChecker.REDIS_CONNECTION_FAIL_MSG, exception.getMessage());
    assertEquals(connectionTimeoutMsg, exception.getCause().getMessage());
  }
}
