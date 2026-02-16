package com.jawisimo.tbcfstarter.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
@EnableAsync
@RequiredArgsConstructor
public class BotConfig {
  private final BotProperties properties;

  @Bean
  public TelegramClient telegramClient() {
    log.info("Telegram bot {} register: {}", properties.name(), LocalTime.now());
    return new OkHttpTelegramClient(properties.token());
  }

  @Bean
  public Executor asyncBotVirtualExecutor() {
    return Executors.newVirtualThreadPerTaskExecutor();
  }
}
