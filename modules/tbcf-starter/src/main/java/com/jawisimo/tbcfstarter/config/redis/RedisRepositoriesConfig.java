package com.jawisimo.tbcfstarter.config.redis;

import com.jawisimo.tbcfstarter.annotation.UserStatePersistent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@UserStatePersistent
@ConditionalOnClass(name = "org.springframework.data.redis.connection.RedisConnectionFactory")
@EnableRedisRepositories(basePackages = "com.jawisimo.tbcfstarter.repository")
public class RedisRepositoriesConfig {
}

