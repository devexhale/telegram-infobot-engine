package com.github.jawisimo.botengine.config.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Core configuration for Telegram bot integration within the bot-engine framework.
 *
 * <p>Enables binding of {@link BotProperties} configuration, and defines infrastructure beans
 * required for communication with the Telegram Bot API.
 *
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties(BotProperties.class)
@RequiredArgsConstructor
public class BotConfig {

  private final BotProperties properties;

  /**
   * Creates and configures a {@link TelegramClient} used for communication with the Telegram Bot
   * API.
   *
   * <p>The client is initialized using the bot token defined in {@link BotProperties}.
   *
   * @return the configured {@link TelegramClient} instance
   */
  @Bean
  public TelegramClient telegramClient() {
    return new OkHttpTelegramClient(properties.token());
  }
}
