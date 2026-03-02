package com.github.jawisimo.botengine.config;

import java.time.LocalTime;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Core configuration for bot-engine infrastructure beans.
 *
 * <p>Enables configuration properties, caching, and asynchronous execution, and provides beans
 * required for Telegram communication and update processing.
 *
 * @since 1.0
 */
@Configuration
@Slf4j
@EnableConfigurationProperties(BotProperties.class)
@EnableCaching
@EnableAsync
@RequiredArgsConstructor
public class BotConfig {

  private final BotProperties properties;

  /**
   * Creates a {@link TelegramClient} used to communicate with the Telegram API.
   *
   * @return the configured Telegram client
   */
  @Bean
  public TelegramClient telegramClient() {
    log.info("Telegram bot {} register: {}", properties.name(), LocalTime.now());
    return new OkHttpTelegramClient(properties.token());
  }

  /**
   * Provides a virtual-thread executor for asynchronous bot update processing.
   *
   * @return the executor used by async bot operations
   */
  @Bean
  public Executor asyncBotVirtualExecutor() {
    return Executors.newVirtualThreadPerTaskExecutor();
  }
}
