package com.github.jawisimo.botengine.config.redis;

import com.github.jawisimo.botengine.config.BotProperties;
import com.github.jawisimo.botengine.exception.RedisConnectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Performs a startup check to ensure that Redis dependency is available when persistent user state
 * is enabled.
 *
 * <p>If {@code telegram.bot.user-state-persistent=true}, this configuration verifies that a Redis
 * connection factory is present in the application context. If Redis dependency is missing, the
 * application fails fast with {@link RedisConnectionException} and provides guidance on how to add
 * the required dependency.
 *
 * @since 1.0
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class RedisCheckConfig {

  private static final String REDIS_DEPENDENCY_FAIL_MSG =
      "Redis is required but not found in the application context. "
          + "Please add 'spring-boot-starter-data-redis'.";

  private final BotProperties properties;
  private final ApplicationContext context;

  /**
   * Creates an {@link ApplicationRunner} that verifies Redis dependency presence when persistent
   * user state is enabled.
   *
   * @return the runner that checks Redis dependency availability
   */
  @Bean
  public ApplicationRunner redisDependencyCheckRunner() {
    return args -> {
      if (!properties.userStatePersistent()) {
        return;
      }

      if (!context.containsBean("redisConnectionFactory")) {
        log.error(REDIS_DEPENDENCY_FAIL_MSG);
        throw new RedisConnectionException(REDIS_DEPENDENCY_FAIL_MSG);
      }
    };
  }
}
