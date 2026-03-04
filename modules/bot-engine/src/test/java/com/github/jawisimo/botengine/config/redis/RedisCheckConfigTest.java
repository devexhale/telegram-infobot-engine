package com.github.jawisimo.botengine.config.redis;

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

@ExtendWith(MockitoExtension.class)
class RedisCheckConfigTest {

  private static final String REDIS_CONNECTION_FACTORY_BEAN_NAME = "redisConnectionFactory";

  private static final String REDIS_DEPENDENCY_FAIL_MSG =
      "Redis is required but not found in the application context. "
          + "Please add 'spring-boot-starter-data-redis'.";

  @Mock private BotProperties properties;
  @Mock private ApplicationContext context;

  @InjectMocks private RedisCheckConfig config;

  @Test
  void redisDependencyCheckRunner_shouldDoNothing_whenUserStatePersistentIsFalse()
      throws Exception {
    ApplicationRunner runner = config.redisDependencyCheckRunner();

    runner.run(null);

    verify(properties).userStatePersistent();
    verifyNoInteractions(context);
  }

  @Test
  void redisDependencyCheckRunner_shouldThrowException_whenRedisDependencyMissing() {
    when(properties.userStatePersistent()).thenReturn(true);
    when(context.containsBean(REDIS_CONNECTION_FACTORY_BEAN_NAME)).thenReturn(false);

    ApplicationRunner runner = config.redisDependencyCheckRunner();

    RedisConnectionException ex =
        assertThrows(RedisConnectionException.class, () -> runner.run(null));

    assertEquals(REDIS_DEPENDENCY_FAIL_MSG, ex.getMessage());
    verify(properties).userStatePersistent();
    verify(context).containsBean(REDIS_CONNECTION_FACTORY_BEAN_NAME);
    verifyNoMoreInteractions(context);
  }

  @Test
  void redisDependencyCheckRunner_shouldPassSuccessfully_whenRedisDependencyPresent()
      throws Exception {
    when(properties.userStatePersistent()).thenReturn(true);
    when(context.containsBean(REDIS_CONNECTION_FACTORY_BEAN_NAME)).thenReturn(true);

    ApplicationRunner runner = config.redisDependencyCheckRunner();

    runner.run(null);

    verify(properties).userStatePersistent();
    verify(context).containsBean(REDIS_CONNECTION_FACTORY_BEAN_NAME);
    verifyNoMoreInteractions(context);
  }

  @Test
  void redisDependencyCheckRunner_shouldLogError_whenRedisDependencyMissing() {
    ListAppender<ILoggingEvent> listAppender = getListAppender();

    when(properties.userStatePersistent()).thenReturn(true);
    when(context.containsBean(REDIS_CONNECTION_FACTORY_BEAN_NAME)).thenReturn(false);

    ApplicationRunner runner = config.redisDependencyCheckRunner();
    assertThrows(RedisConnectionException.class, () -> runner.run(null));

    ILoggingEvent event = listAppender.list.getFirst();
    assertEquals(Level.ERROR, event.getLevel());
    assertTrue(event.getFormattedMessage().contains(REDIS_DEPENDENCY_FAIL_MSG));
  }

  private ListAppender<ILoggingEvent> getListAppender() {
    Logger logger = (Logger) LoggerFactory.getLogger(RedisCheckConfig.class);
    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);
    return listAppender;
  }
}
