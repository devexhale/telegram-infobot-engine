package io.github.devexhale.botengine.config;

import io.github.devexhale.botengine.properties.BotProperties;
import io.github.devexhale.botengine.properties.BroadcastProperties;
import io.github.devexhale.botengine.properties.DialogProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Provides core configuration for Telegram bot integration.
 *
 * <p>Enables configuration properties binding and defines infrastructure beans for Telegram Bot API
 * communication.
 *
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties({
  BotProperties.class,
  DialogProperties.class,
  BroadcastProperties.class
})
@RequiredArgsConstructor
public class BotConfig {

  private final BotProperties properties;

  /**
   * Creates a {@link TelegramClient} for Telegram Bot API communication.
   *
   * <p>Initialized using the bot token from {@link BotProperties}.
   *
   * @return the configured {@link TelegramClient} instance
   */
  @Bean("telegramClient")
  public TelegramClient telegramClient() {
    return new OkHttpTelegramClient(properties.token());
  }
}
