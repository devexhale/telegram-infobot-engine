package com.github.jawisimo.tbcfstarter.config.redis;

import com.github.jawisimo.tbcfstarter.annotation.UserStatePersistent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@UserStatePersistent
@ConditionalOnClass(name = "org.springframework.data.redis.connection.RedisConnectionFactory")
@EnableRedisRepositories(basePackages = "com.github.jawisimo.tbcfstarter.repository")
public class RedisRepositoriesConfig {}
