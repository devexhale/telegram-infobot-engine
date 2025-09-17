package com.jawisimo.tbcfstarter.config;

import com.jawisimo.tbcfstarter.annotation.UserStatePersistent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.LocalTime;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@Slf4j
@EnableConfigurationProperties(BotProperties.class)
@EnableCaching
@EnableRedisRepositories(basePackages = "com.jawisimo.tbcfstarter.repository")
@EnableAsync
@RequiredArgsConstructor
public class BotConfig {
    private final BotProperties properties;

    private static final String REDIS_FAIL_MESSAGE = "❌ Redis is required but not found in the application context.\n" +
            "Please add the Redis dependency (e.g., spring-boot-starter-data-redis) " +
            "and configure a RedisConnectionFactory either via application properties " +
            "(spring.redis.*) or Java configuration.";


    @Bean
    public TelegramClient telegramClient() {
        log.info("Telegram bot {} register: {}", properties.name(), LocalTime.now());
        return new OkHttpTelegramClient(properties.token());
    }

    @Bean
    public Executor asyncBotVirtualExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean
    @UserStatePersistent
    public static BeanFactoryPostProcessor redisCheckPostProcessor() {
        return beanFactory -> {
            if (!beanFactory.containsBeanDefinition("redisConnectionFactory")) {
                throw new IllegalStateException(REDIS_FAIL_MESSAGE);
            }
        };
    }

}
