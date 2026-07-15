package io.github.devexhale.botengine.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.devexhale.botengine.diagnostics.exception.RedisInitializationException;
import io.github.devexhale.botengine.testsupport.logger.TestLogCaptor;
import java.util.List;
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
  private static final String CHECK_START_LOG = "Checking Redis connectivity...";
  private static final String CHECK_PASS_LOG = "Redis connectivity check passed in";
  private static final String CHECK_FAIL_LOG = "Redis connectivity check failed after";

  @Mock private ApplicationContext context;
  @Mock private RedisConnectionFactory redisConnectionFactory;
  @Mock private RedisConnection redisConnection;

  @InjectMocks private RedisFailFastChecker redisFailFastChecker;

  @Test
  void check_shouldPassSuccessfully_whenPingReturnsPong() {
    when(context.getBean(REDIS_CONNECTION_FACTORY_BEAN_NAME)).thenReturn(redisConnectionFactory);
    when(redisConnectionFactory.getConnection()).thenReturn(redisConnection);
    when(redisConnection.ping()).thenReturn("PONG");

    try (TestLogCaptor logCaptor = new TestLogCaptor(RedisFailFastChecker.class)) {
      assertDoesNotThrow(() -> redisFailFastChecker.check());

      List<ILoggingEvent> events = logCaptor.events();
      assertTrue(containsLog(events, Level.INFO, CHECK_START_LOG));
      assertTrue(containsLog(events, Level.INFO, CHECK_PASS_LOG));
    }
  }

  @Test
  void check_shouldThrowException_whenPingReturnsNotPong() {
    when(context.getBean(REDIS_CONNECTION_FACTORY_BEAN_NAME)).thenReturn(redisConnectionFactory);
    when(redisConnectionFactory.getConnection()).thenReturn(redisConnection);
    when(redisConnection.ping()).thenReturn("NOT_PONG");

    try (TestLogCaptor logCaptor = new TestLogCaptor(RedisFailFastChecker.class)) {
      RedisInitializationException exception =
          assertThrows(RedisInitializationException.class, () -> redisFailFastChecker.check());

      assertEquals(RedisFailFastChecker.CONNECTION_FAIL_MSG, exception.getMessage());

      List<ILoggingEvent> events = logCaptor.events();
      assertTrue(containsLog(events, Level.INFO, CHECK_START_LOG));
      assertTrue(containsLog(events, Level.ERROR, CHECK_FAIL_LOG));
    }
  }

  @Test
  void check_shouldThrowWrappedException_whenConnectionFails() {
    String connectionTimeoutMsg = "Connection timeout";

    when(context.getBean(REDIS_CONNECTION_FACTORY_BEAN_NAME)).thenReturn(redisConnectionFactory);
    when(redisConnectionFactory.getConnection())
        .thenThrow(new RuntimeException(connectionTimeoutMsg));

    try (TestLogCaptor logCaptor = new TestLogCaptor(RedisFailFastChecker.class)) {
      RedisInitializationException exception =
          assertThrows(RedisInitializationException.class, () -> redisFailFastChecker.check());

      assertEquals(RedisFailFastChecker.CONNECTION_FAIL_MSG, exception.getMessage());
      assertEquals(connectionTimeoutMsg, exception.getCause().getMessage());

      List<ILoggingEvent> events = logCaptor.events();
      assertTrue(containsLog(events, Level.INFO, CHECK_START_LOG));
      assertTrue(containsLog(events, Level.ERROR, CHECK_FAIL_LOG));
    }
  }

  private boolean containsLog(List<ILoggingEvent> events, Level level, String message) {
    return events.stream()
        .anyMatch(
            event -> event.getLevel() == level && event.getFormattedMessage().contains(message));
  }
}
