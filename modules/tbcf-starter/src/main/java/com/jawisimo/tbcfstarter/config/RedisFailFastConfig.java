package com.jawisimo.tbcfstarter.config;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisFailFastConfig {
    private static final String REDIS_FAIL_MESSAGE = "❌ Redis is required but not found in the application context.\n" +
            "Please add the Redis dependency (e.g., spring-boot-starter-data-redis) " +
            "and configure a RedisConnectionFactory either via application properties " +
            "(spring.redis.*) or Java configuration.";

    @Bean
    @ConditionalOnProperty(prefix = "telegram.bot", name = "enable-last-command", havingValue = "true")
    public static BeanFactoryPostProcessor redisCheckPostProcessor() {
        return beanFactory -> {
            if (!beanFactory.containsBeanDefinition("redisConnectionFactory")) {
                throw new IllegalStateException(REDIS_FAIL_MESSAGE);
            }
        };
    }
}
