package com.github.jawisimo.botengine.config.redis;

import com.github.jawisimo.botengine.annotation.UserStatePersistent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

/**
 * Enables Redis repositories when persistent user state support is active.
 *
 * <p>Activates Spring Data Redis repository scanning only when Redis is available on the classpath
 * and {@link UserStatePersistent} is enabled.
 *
 * @since 1.0
 */
@Configuration
@UserStatePersistent
@ConditionalOnClass(name = "org.springframework.data.redis.connection.RedisConnectionFactory")
@EnableRedisRepositories(basePackages = "com.github.jawisimo.botengine.repository")
public class RedisRepositoryConfig {}
