package com.github.jawisimo.botengine.core;

import com.github.jawisimo.botengine.interaction.command.CommandsInitializer;
import com.github.jawisimo.botengine.service.UpdateService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Consumes incoming Telegram updates and delegates them to the framework processing pipeline.
 *
 * <p>Acts as a bridge between the Telegram long polling infrastructure and the bot-engine
 * framework. Receives updates from Telegram and forwards them to {@link UpdateService}, which
 * orchestrates dialog execution.
 *
 * <p>Operates in a single-threaded mode provided by {@link LongPollingSingleThreadUpdateConsumer},
 * ensuring sequential update processing and predictable user state transitions.
 *
 * <p>Execution flow: TelegramBot -> UpdateConsumer -> UpdateService -> dialog execution pipeline.
 *
 * @since 1.0
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {

  private final CommandsInitializer commandsInitializer;
  private final UpdateService updateService;

  /** Initializes bot commands after the Spring context is fully constructed. */
  @PostConstruct
  public void init() {
    commandsInitializer.setUpCommands();
  }

  /**
   * Consumes a Telegram {@link Update} and delegates it to the update service.
   *
   * @param update the incoming Telegram update
   */
  @Override
  public void consume(Update update) {
    updateService.onUpdateReceived(update);
  }
}
