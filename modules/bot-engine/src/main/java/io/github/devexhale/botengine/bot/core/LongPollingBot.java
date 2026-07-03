package io.github.devexhale.botengine.bot.core;

import io.github.devexhale.botengine.properties.BotProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;

/**
 * Spring-managed Telegram bot with long polling.
 *
 * <p>Provides token from {@link BotProperties} and delegates updates to {@link UpdateConsumer}.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class LongPollingBot implements SpringLongPollingBot {

  private final BotProperties properties;
  private final UpdateConsumer updateConsumer;

  /**
   * Returns the bot authentication token.
   *
   * @return the configured bot token
   */
  @Override
  public String getBotToken() {
    return properties.token();
  }

  /**
   * Returns the consumer that processes incoming updates.
   *
   * @return the {@link UpdateConsumer} instance
   */
  @Override
  public LongPollingUpdateConsumer getUpdatesConsumer() {
    return updateConsumer;
  }
}
