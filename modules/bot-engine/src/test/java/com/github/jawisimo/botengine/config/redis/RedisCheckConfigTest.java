package com.github.jawisimo.botengine.config.redis;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.github.jawisimo.botengine.config.BotProperties;
import com.github.jawisimo.botengine.exception.RedisConnectionException;
import com.github.jawisimo.botengine.util.EnvironmentDetector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@ExtendWith(MockitoExtension.class)
class RedisCheckConfigTest {

  public static final String REDIS_DEPENDENCY_FAIL_MSG =
      "Redis is required but not found in the application context. "
          + "Please add spring-boot-starter-data-redis "
          + "and configure spring.data.redis.*properties.";

  public static final String REDIS_CONNECTION_FAIL_MSG =
      "Connection to Redis failed. Please check your connection.";

  public static final String SOME_ERROR_MSG = "Some error message...";

  public static final String NOPE = "NOPE";
  public static final String PONG = "PONG";

  public static final String REDIS_CONNECTION_FACTORY_BEAN_NAME = "redisConnectionFactory";

  @Mock private BotProperties properties;
  @Mock private ApplicationContext context;
  @Mock private RedisConnectionFactory redisConnectionFactory;
  @Mock private RedisConnection redisConnection;

  @InjectMocks private RedisCheckConfig config;

  private MockedStatic<EnvironmentDetector> detector;

  @BeforeEach
  void setUp() {
    detector = mockStatic(EnvironmentDetector.class);
    detector.when(EnvironmentDetector::isTestEnvironment).thenReturn(false);
  }

  @AfterEach
  void tearDown() {
    detector.close();
  }

  @Test
  void redisCheckRunner_shouldDoNothing_whenUserStatePersistentIsFalse() throws Exception {
    ApplicationRunner runner = config.redisCheckRunner();
    runner.run(null);

    verify(properties).userStatePersistent();
    verifyNoInteractions(context);
  }

  @Test
  void redisCheckRunner_shouldDoNothing_whenIsTestEnvironment() throws Exception {
    detector.when(EnvironmentDetector::isTestEnvironment).thenReturn(true);
    when(properties.userStatePersistent()).thenReturn(true);

    ApplicationRunner runner = config.redisCheckRunner();
    runner.run(null);

    verifyNoInteractions(context);
  }

  @Test
  void redisCheckRunner_shouldThrowException_whenRedisDependencyMissing() {
    when(properties.userStatePersistent()).thenReturn(true);
    when(context.containsBean(REDIS_CONNECTION_FACTORY_BEAN_NAME)).thenReturn(false);

    ApplicationRunner runner = config.redisCheckRunner();

    RedisConnectionException ex =
        assertThrows(RedisConnectionException.class, () -> runner.run(null));

    assertEquals(REDIS_DEPENDENCY_FAIL_MSG, ex.getMessage());
    verify(properties).userStatePersistent();
    verify(context).containsBean(REDIS_CONNECTION_FACTORY_BEAN_NAME);
    verify(context, never()).getBean(LettuceConnectionFactory.class);
  }

  @Test
  void redisCheckRunner_shouldPassSuccessfully_whenPingReturnsPong() throws Exception {
    when(properties.userStatePersistent()).thenReturn(true);
    when(context.containsBean(REDIS_CONNECTION_FACTORY_BEAN_NAME)).thenReturn(true);
    when(context.getBean(REDIS_CONNECTION_FACTORY_BEAN_NAME)).thenReturn(redisConnectionFactory);
    when(redisConnectionFactory.getConnection()).thenReturn(redisConnection);
    when(redisConnection.ping()).thenReturn(PONG);

    ApplicationRunner runner = config.redisCheckRunner();
    runner.run(null);

    verify(properties).userStatePersistent();
    verify(context).containsBean(REDIS_CONNECTION_FACTORY_BEAN_NAME);
    verify(context).getBean(REDIS_CONNECTION_FACTORY_BEAN_NAME);
    verify(redisConnectionFactory).getConnection();
    verify(redisConnection).ping();
    verify(redisConnection).close();
  }

  @Test
  void redisCheckRunner_shouldThrowConnectFail_whenPingIsNotPong() {
    when(properties.userStatePersistent()).thenReturn(true);
    when(context.containsBean(REDIS_CONNECTION_FACTORY_BEAN_NAME)).thenReturn(true);
    when(context.getBean(REDIS_CONNECTION_FACTORY_BEAN_NAME)).thenReturn(redisConnectionFactory);
    when(redisConnectionFactory.getConnection()).thenReturn(redisConnection);
    when(redisConnection.ping()).thenReturn(NOPE);

    ApplicationRunner runner = config.redisCheckRunner();

    RedisConnectionException ex =
        assertThrows(RedisConnectionException.class, () -> runner.run(null));

    assertEquals(REDIS_CONNECTION_FAIL_MSG, ex.getMessage());
    verify(redisConnection).close();
  }

  @Test
  void redisCheckRunner_shouldThrowConnectFail_whenConnectionFactoryThrowsException() {
    when(properties.userStatePersistent()).thenReturn(true);
    when(context.containsBean(REDIS_CONNECTION_FACTORY_BEAN_NAME)).thenReturn(true);
    when(context.getBean(REDIS_CONNECTION_FACTORY_BEAN_NAME)).thenReturn(redisConnectionFactory);
    when(redisConnectionFactory.getConnection()).thenThrow(new RuntimeException(SOME_ERROR_MSG));

    ApplicationRunner runner = config.redisCheckRunner();

    RedisConnectionException ex =
        assertThrows(RedisConnectionException.class, () -> runner.run(null));

    assertEquals(REDIS_CONNECTION_FAIL_MSG, ex.getMessage());
    assertNotNull(ex.getCause());
    assertEquals(SOME_ERROR_MSG, ex.getCause().getMessage());
    verify(redisConnectionFactory).getConnection();
    verifyNoInteractions(redisConnection);
  }

  @Test
  void redisCheckRunner_shouldLogError_whenRedisDependencyMissing() {
    ListAppender<ILoggingEvent> listAppender = getListAppender();
    when(properties.userStatePersistent()).thenReturn(true);
    when(context.containsBean(REDIS_CONNECTION_FACTORY_BEAN_NAME)).thenReturn(false);

    ApplicationRunner runner = config.redisCheckRunner();
    assertThrows(RedisConnectionException.class, () -> runner.run(null));

    ILoggingEvent event = listAppender.list.getFirst();
    assertEquals(Level.ERROR, event.getLevel());
    assertTrue(event.getFormattedMessage().contains(REDIS_DEPENDENCY_FAIL_MSG));
  }

  @Test
  void redisCheckRunner_shouldLogError_whenPingIsNotPong() {
    ListAppender<ILoggingEvent> listAppender = getListAppender();
    when(properties.userStatePersistent()).thenReturn(true);
    when(context.containsBean(REDIS_CONNECTION_FACTORY_BEAN_NAME)).thenReturn(true);
    when(context.getBean(REDIS_CONNECTION_FACTORY_BEAN_NAME)).thenReturn(redisConnectionFactory);
    when(redisConnectionFactory.getConnection()).thenReturn(redisConnection);
    when(redisConnection.ping()).thenReturn(NOPE);

    ApplicationRunner runner = config.redisCheckRunner();
    assertThrows(RedisConnectionException.class, () -> runner.run(null));

    ILoggingEvent event = listAppender.list.getFirst();
    assertEquals(Level.ERROR, event.getLevel());
    assertTrue(event.getFormattedMessage().contains(REDIS_CONNECTION_FAIL_MSG));
  }

  @Test
  void redisCheckRunner_shouldLogError_whenConnectionFactoryThrowsException() {
    ListAppender<ILoggingEvent> listAppender = getListAppender();
    when(properties.userStatePersistent()).thenReturn(true);
    when(context.containsBean(REDIS_CONNECTION_FACTORY_BEAN_NAME)).thenReturn(true);
    when(context.getBean(REDIS_CONNECTION_FACTORY_BEAN_NAME)).thenReturn(redisConnectionFactory);
    when(redisConnectionFactory.getConnection()).thenThrow(new RuntimeException(SOME_ERROR_MSG));

    ApplicationRunner runner = config.redisCheckRunner();
    assertThrows(RedisConnectionException.class, () -> runner.run(null));

    ILoggingEvent event = listAppender.list.getFirst();
    assertEquals(Level.ERROR, event.getLevel());
    assertTrue(event.getFormattedMessage().contains(REDIS_CONNECTION_FAIL_MSG));
  }

  private ListAppender<ILoggingEvent> getListAppender() {
    Logger logger = (Logger) LoggerFactory.getLogger(RedisCheckConfig.class);
    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);
    return listAppender;
  }
}
