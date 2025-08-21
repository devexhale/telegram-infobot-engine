package com.jawisimo.tbcfstarter.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.LocalTime;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@Slf4j
@EnableConfigurationProperties(BotProperties.class)
@EnableAsync
@RequiredArgsConstructor
public class BotConfig {
    private final BotProperties properties;

    @Bean
    public TelegramClient telegramClient() {
        log.info("Telegram bot register: {}", LocalTime.now());
        return new OkHttpTelegramClient(properties.token());
    }

    @Bean(name = "asyncBotExecutor")
    public Executor asyncBotExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.executorCorePoolSize());
        executor.setMaxPoolSize(properties.executorMaxPoolSize());
        executor.setQueueCapacity(properties.executorQueueCapacity());
        executor.setThreadNamePrefix(properties.executorThreadNamePrefix());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

}
