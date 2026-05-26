package io.github.devexhale.botengine.bot.core;

import io.github.devexhale.botengine.properties.BotProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;

/**
 * Entry point of the bots-engine framework representing a Spring-managed Telegram bot.
 *
 * <p>Integrates with Telegram long polling via {@link SpringLongPollingBot}. Supplies the bot token
 * from {@link BotProperties} and delegates incoming updates to {@link UpdateConsumer}, which starts
 * the update processing lifecycle.
 *
 * <p>Execution flow: Telegram API -> TelegramBot -> UpdateConsumer -> UpdateDispatcher -> dialog
 * execution pipeline.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class LongPollingBot implements SpringLongPollingBot {

  private final BotProperties properties;
  private final UpdateConsumer updateConsumer;

  /**
   * Returns the Telegram bot authentication token.
   *
   * @return the configured bot token
   */
  @Override
  public String getBotToken() {
    return properties.token();
  }

  /**
   * Returns the update consumer for this bot.
   *
   * @return the {@link UpdateConsumer} that processes updates
   */
  @Override
  public LongPollingUpdateConsumer getUpdatesConsumer() {
    return updateConsumer;
  }
}
