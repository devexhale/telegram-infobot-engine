package io.github.devexhale.botengine.application;

import io.github.devexhale.botengine.diagnostics.exception.RedisInitializationException;
import io.github.devexhale.botengine.util.EnvironmentDetector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisFailFastCheckerTest {

  private static final String REDIS_BEAN_NAME = "redisConnectionFactory";

  @Mock private ApplicationContext context;

  @InjectMocks private RedisFailFastChecker redisFailFastChecker;

  private MockedStatic<EnvironmentDetector> mockedEnvironmentDetector;

  @BeforeEach
  void setUp() {
    mockedEnvironmentDetector = mockStatic(EnvironmentDetector.class);
  }

  @AfterEach
  void tearDown() {
    mockedEnvironmentDetector.close();
  }

  @Test
  void check_shouldSkipExecution_whenTestEnvironment() {
    mockedEnvironmentDetector.when(EnvironmentDetector::isTestEnvironment).thenReturn(true);

    assertDoesNotThrow(() -> redisFailFastChecker.check());
    verifyNoInteractions(context);
  }

  @Test
  void check_shouldThrowException_whenBeanNotFound() {
    mockedEnvironmentDetector.when(EnvironmentDetector::isTestEnvironment).thenReturn(false);

    when(context.containsBean(REDIS_BEAN_NAME)).thenReturn(false);

    RedisInitializationException exception =
        assertThrows(RedisInitializationException.class, () -> redisFailFastChecker.check());

    assertEquals(
        "Redis is required but not found in the application context.", exception.getMessage());
  }

  @Test
  void check_shouldPassSuccessfully_whenPingReturnsPong() {
    mockedEnvironmentDetector.when(EnvironmentDetector::isTestEnvironment).thenReturn(false);
    when(context.containsBean(REDIS_BEAN_NAME)).thenReturn(true);

    RedisConnectionFactory factory = mock(RedisConnectionFactory.class);
    RedisConnection connection = mock(RedisConnection.class);

    when(context.getBean(REDIS_BEAN_NAME)).thenReturn(factory);
    when(factory.getConnection()).thenReturn(connection);
    when(connection.ping()).thenReturn("PONG");

    assertDoesNotThrow(() -> redisFailFastChecker.check());
  }

  @Test
  void check_shouldThrowException_whenPingReturnsNotPong() {
    mockedEnvironmentDetector.when(EnvironmentDetector::isTestEnvironment).thenReturn(false);
    when(context.containsBean(REDIS_BEAN_NAME)).thenReturn(true);

    RedisConnectionFactory factory = mock(RedisConnectionFactory.class);
    RedisConnection connection = mock(RedisConnection.class);

    when(context.getBean(REDIS_BEAN_NAME)).thenReturn(factory);
    when(factory.getConnection()).thenReturn(connection);
    when(connection.ping()).thenReturn("NOT_PONG");

    RedisInitializationException exception =
        assertThrows(RedisInitializationException.class, () -> redisFailFastChecker.check());

    assertEquals(RedisFailFastChecker.REDIS_CONNECTION_FAIL_MSG, exception.getMessage());
  }

  @Test
  void check_shouldThrowWrappedException_whenConnectionFails() {
    String connectionTimeoutMsg = "Connection timeout";

    mockedEnvironmentDetector.when(EnvironmentDetector::isTestEnvironment).thenReturn(false);
    when(context.containsBean(REDIS_BEAN_NAME)).thenReturn(true);

    RedisConnectionFactory factory = mock(RedisConnectionFactory.class);
    when(context.getBean(REDIS_BEAN_NAME)).thenReturn(factory);
    when(factory.getConnection()).thenThrow(new RuntimeException(connectionTimeoutMsg));

    RedisInitializationException exception =
        assertThrows(RedisInitializationException.class, () -> redisFailFastChecker.check());

    assertEquals(RedisFailFastChecker.REDIS_CONNECTION_FAIL_MSG, exception.getMessage());
    assertEquals(connectionTimeoutMsg, exception.getCause().getMessage());
  }
}
