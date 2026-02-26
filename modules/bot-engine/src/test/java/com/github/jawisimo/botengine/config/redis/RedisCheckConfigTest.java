package com.github.jawisimo.botengine.config.redis;

import static com.github.jawisimo.botengine.config.redis.RedisTestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.github.jawisimo.botengine.config.BotProperties;
import com.github.jawisimo.botengine.exception.RedisConnectionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@ExtendWith(MockitoExtension.class)
class RedisCheckConfigTest {
  @Mock private BotProperties properties;
  @Mock private ApplicationContext context;
  @Mock private LettuceConnectionFactory redisConnectionFactory;
  @Mock private RedisConnection redisConnection;

  @InjectMocks private RedisCheckConfig config;

  @Test
  void redisCheckRunner_shouldDoNothing_whenUserStatePersistentIsFalse() throws Exception {
    ApplicationRunner runner = config.redisCheckRunner();

    runner.run(null);

    verify(properties).userStatePersistent();
    verifyNoInteractions(context);
  }

  @Test
  void redisCheckRunner_shouldThrowException_whenRedisDependencyMissing() {
    when(properties.userStatePersistent()).thenReturn(true);
    when(context.containsBean(CONNECTION_FACTORY_NAME)).thenReturn(false);

    ApplicationRunner runner = config.redisCheckRunner();

    RedisConnectionException ex =
        assertThrows(RedisConnectionException.class, () -> runner.run(null));

    assertEquals(REDIS_DEPENDENCY_FAIL_MESSAGE, ex.getMessage());
    verify(properties).userStatePersistent();
    verify(context).containsBean(CONNECTION_FACTORY_NAME);
    verify(context, never()).getBean(LettuceConnectionFactory.class);
  }

  @Test
  void redisCheckRunner_shouldPassSuccessfully_whenPingReturnsPong() throws Exception {
    when(properties.userStatePersistent()).thenReturn(true);
    when(context.containsBean(CONNECTION_FACTORY_NAME)).thenReturn(true);
    when(context.getBean(LettuceConnectionFactory.class)).thenReturn(redisConnectionFactory);
    when(redisConnectionFactory.getConnection()).thenReturn(redisConnection);
    when(redisConnection.ping()).thenReturn(PONG);

    ApplicationRunner runner = config.redisCheckRunner();

    runner.run(null);

    verify(properties).userStatePersistent();
    verify(context).containsBean(CONNECTION_FACTORY_NAME);
    verify(context).getBean(LettuceConnectionFactory.class);
    verify(redisConnectionFactory).getConnection();
    verify(redisConnection).ping();
    verify(redisConnection).close();
  }

  @Test
  void redisCheckRunner_shouldThrowConnectFail_whenPingIsNotPong() {
    when(properties.userStatePersistent()).thenReturn(true);
    when(context.containsBean(CONNECTION_FACTORY_NAME)).thenReturn(true);
    when(context.getBean(LettuceConnectionFactory.class)).thenReturn(redisConnectionFactory);
    when(redisConnectionFactory.getConnection()).thenReturn(redisConnection);
    when(redisConnection.ping()).thenReturn(NOPE);

    ApplicationRunner runner = config.redisCheckRunner();

    RedisConnectionException ex =
        assertThrows(RedisConnectionException.class, () -> runner.run(null));

    assertEquals(REDIS_CONNECT_FAIL_MESSAGE, ex.getMessage());
    assertNotNull(ex.getCause());
    RedisConnectionException cause =
        assertInstanceOf(RedisConnectionException.class, ex.getCause());
    assertEquals(REDIS_PING_FAIL_MESSAGE, cause.getMessage());
    verify(redisConnection).close();
  }

  @Test
  void redisCheckRunner_shouldThrowConnectFail_whenConnectionFactoryThrowsException() {
    when(properties.userStatePersistent()).thenReturn(true);
    when(context.containsBean(CONNECTION_FACTORY_NAME)).thenReturn(true);
    when(context.getBean(LettuceConnectionFactory.class)).thenReturn(redisConnectionFactory);
    when(redisConnectionFactory.getConnection())
        .thenThrow(new RuntimeException(SOME_ERROR_MESSAGE));

    ApplicationRunner runner = config.redisCheckRunner();

    RedisConnectionException ex =
        assertThrows(RedisConnectionException.class, () -> runner.run(null));

    assertEquals(REDIS_CONNECT_FAIL_MESSAGE, ex.getMessage());
    assertNotNull(ex.getCause());
    assertEquals(SOME_ERROR_MESSAGE, ex.getCause().getMessage());
    verify(redisConnectionFactory).getConnection();
    verifyNoInteractions(redisConnection);
  }

  @Test
  void redisCheckRunner_shouldLogError_whenRedisDependencyMissing() {
    ListAppender<ILoggingEvent> listAppender = getListAppender();

    when(properties.userStatePersistent()).thenReturn(true);
    when(context.containsBean(CONNECTION_FACTORY_NAME)).thenReturn(false);

    ApplicationRunner runner = config.redisCheckRunner();
    assertThrows(RedisConnectionException.class, () -> runner.run(null));

    ILoggingEvent event = listAppender.list.getFirst();
    assertEquals(Level.ERROR, event.getLevel());
    assertTrue(event.getFormattedMessage().contains(REDIS_DEPENDENCY_FAIL_MESSAGE));
  }

  @Test
  void redisCheckRunner_shouldLogError_whenPingIsNotPong() {
    ListAppender<ILoggingEvent> listAppender = getListAppender();

    when(properties.userStatePersistent()).thenReturn(true);
    when(context.containsBean(CONNECTION_FACTORY_NAME)).thenReturn(true);
    when(context.getBean(LettuceConnectionFactory.class)).thenReturn(redisConnectionFactory);
    when(redisConnectionFactory.getConnection()).thenReturn(redisConnection);
    when(redisConnection.ping()).thenReturn(NOPE);

    ApplicationRunner runner = config.redisCheckRunner();
    assertThrows(RedisConnectionException.class, () -> runner.run(null));

    ILoggingEvent event = listAppender.list.getFirst();
    assertEquals(Level.ERROR, event.getLevel());
    assertTrue(event.getFormattedMessage().contains(REDIS_PING_FAIL_MESSAGE));
  }

  @Test
  void redisCheckRunner_shouldLogError_whenConnectionFactoryThrowsException() {
    ListAppender<ILoggingEvent> listAppender = getListAppender();

    when(properties.userStatePersistent()).thenReturn(true);
    when(context.containsBean(CONNECTION_FACTORY_NAME)).thenReturn(true);
    when(context.getBean(LettuceConnectionFactory.class)).thenReturn(redisConnectionFactory);
    when(redisConnectionFactory.getConnection())
        .thenThrow(new RuntimeException(SOME_ERROR_MESSAGE));

    ApplicationRunner runner = config.redisCheckRunner();
    assertThrows(RedisConnectionException.class, () -> runner.run(null));

    ILoggingEvent event = listAppender.list.getFirst();
    assertEquals(Level.ERROR, event.getLevel());
    assertTrue(event.getFormattedMessage().contains(REDIS_CONNECT_FAIL_MESSAGE));
  }

  private ListAppender<ILoggingEvent> getListAppender() {
    Logger logger = (Logger) LoggerFactory.getLogger(RedisCheckConfig.class);
    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);
    return listAppender;
  }
}
