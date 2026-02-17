package com.github.jawisimo.tbcfstarter.config.redis;

import com.github.jawisimo.tbcfstarter.config.BotProperties;
import com.github.jawisimo.tbcfstarter.exception.RedisConnectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedisCheckConfig {

  private static final String REDIS_DEPENDENCY_FAIL_MESSAGE =
      "❌ Redis is required but not found in the application context. "
          + "Please add spring-boot-starter-data-redis "
          + "and configure spring.data.redis.*properties.";

  private static final String REDIS_CONNECT_FAIL_MESSAGE =
      "❌ Redis dependency is present, but connection failed";

  private static final String REDIS_PING_FAIL_MESSAGE =
      "❌ Redis dependency is present, but cannot ping";

  private static final String REDIS_PONG = "PONG";

  private final BotProperties properties;
  private final ApplicationContext context;

  @Bean
  public ApplicationRunner redisCheckRunner() {
    return args -> {
      if (!properties.userStatePersistent()) {
        return;
      }

      if (!context.containsBean("redisConnectionFactory")) {
        log.error(REDIS_DEPENDENCY_FAIL_MESSAGE);
        throw new RedisConnectionException(REDIS_DEPENDENCY_FAIL_MESSAGE);
      }

      LettuceConnectionFactory redisConnectionFactory =
          context.getBean(LettuceConnectionFactory.class);

      try (RedisConnection redisConnection = redisConnectionFactory.getConnection()) {
        String pongResponse = redisConnection.ping();

        if (!REDIS_PONG.equalsIgnoreCase(pongResponse)) {
          log.error(REDIS_PING_FAIL_MESSAGE);
          throw new RedisConnectionException(REDIS_PING_FAIL_MESSAGE);
        }
      } catch (Exception e) {
        log.error(REDIS_CONNECT_FAIL_MESSAGE, e);
        throw new RedisConnectionException(REDIS_CONNECT_FAIL_MESSAGE, e);
      }
    };
  }
}
