package config.redis;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.github.jawisimo.tbcfstarter.config.BotProperties;
import com.github.jawisimo.tbcfstarter.config.redis.RedisCheckConfig;
import com.github.jawisimo.tbcfstarter.exception.RedisConnectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@ExtendWith(MockitoExtension.class)
class RedisCheckConfigTest {

  private static final String REDIS_DEPENDENCY_FAIL_MESSAGE =
      "❌ Redis is required but not found in the application context. "
          + "Please add spring-boot-starter-data-redis "
          + "and configure spring.data.redis.*properties.";

  private static final String REDIS_CONNECT_FAIL_MESSAGE =
      "❌ Redis dependency is present, but connection failed";

  private static final String REDIS_PING_FAIL_MESSAGE =
      "❌ Redis dependency is present, but cannot ping";

  @Mock private BotProperties properties;
  @Mock private ApplicationContext context;
  @Mock private LettuceConnectionFactory redisConnectionFactory;
  @Mock private RedisConnection redisConnection;

  private RedisCheckConfig config;

  @BeforeEach
  void init() {
    config = new RedisCheckConfig(properties, context);
  }

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
    when(context.containsBean("redisConnectionFactory")).thenReturn(false);

    ApplicationRunner runner = config.redisCheckRunner();

    RedisConnectionException ex =
        assertThrows(RedisConnectionException.class, () -> runner.run(null));

    assertEquals(REDIS_DEPENDENCY_FAIL_MESSAGE, ex.getMessage());
    verify(properties).userStatePersistent();
    verify(context).containsBean("redisConnectionFactory");
    verify(context, never()).getBean(LettuceConnectionFactory.class);
  }

  @Test
  void redisCheckRunner_shouldPassSuccessfully_whenPingReturnsPong() throws Exception {
    when(properties.userStatePersistent()).thenReturn(true);
    when(context.containsBean("redisConnectionFactory")).thenReturn(true);
    when(context.getBean(LettuceConnectionFactory.class)).thenReturn(redisConnectionFactory);
    when(redisConnectionFactory.getConnection()).thenReturn(redisConnection);
    when(redisConnection.ping()).thenReturn("PONG");

    ApplicationRunner runner = config.redisCheckRunner();

    runner.run(null);

    verify(properties).userStatePersistent();
    verify(context).containsBean("redisConnectionFactory");
    verify(context).getBean(LettuceConnectionFactory.class);
    verify(redisConnectionFactory).getConnection();
    verify(redisConnection).ping();
    verify(redisConnection).close();
  }

  @Test
  void redisCheckRunner_shouldThrowConnectFail_whenPingIsNotPong() {
    when(properties.userStatePersistent()).thenReturn(true);
    when(context.containsBean("redisConnectionFactory")).thenReturn(true);
    when(context.getBean(LettuceConnectionFactory.class)).thenReturn(redisConnectionFactory);
    when(redisConnectionFactory.getConnection()).thenReturn(redisConnection);
    when(redisConnection.ping()).thenReturn("NOPE");

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
    when(context.containsBean("redisConnectionFactory")).thenReturn(true);
    when(context.getBean(LettuceConnectionFactory.class)).thenReturn(redisConnectionFactory);
    when(redisConnectionFactory.getConnection()).thenThrow(new RuntimeException("boom"));

    ApplicationRunner runner = config.redisCheckRunner();

    RedisConnectionException ex =
        assertThrows(RedisConnectionException.class, () -> runner.run(null));

    assertEquals(REDIS_CONNECT_FAIL_MESSAGE, ex.getMessage());
    assertNotNull(ex.getCause());
    assertEquals("boom", ex.getCause().getMessage());
    verify(redisConnectionFactory).getConnection();
    verifyNoInteractions(redisConnection);
  }
}
