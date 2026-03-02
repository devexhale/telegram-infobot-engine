package com.github.jawisimo.botengine.service;

import com.github.jawisimo.botengine.interaction.DialogExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Orchestrates processing of incoming Telegram updates within the framework.
 *
 * <p>Delegates supported update types to {@link DialogExecutor}. Message updates are routed to
 * {@code executeMessage}, callback query updates are routed to {@code executeCallback}.
 *
 * <p>This service is executed asynchronously using the {@code asyncBotVirtualExecutor} task
 * executor.
 *
 * @since 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateService {

  private final DialogExecutor executor;

  /**
   * Routes the update to appropriate dialog executor method.
   *
   * @param update the incoming Telegram update
   */
  @Async("asyncBotVirtualExecutor")
  public void onUpdateReceived(Update update) {
    try {
      if (update.hasMessage()) {
        executor.executeMessage(update.getMessage());
      } else if (update.hasCallbackQuery()) {
        executor.executeCallback(update.getCallbackQuery());
      } else {
        log.warn("Unsupported update type: {}", update);
      }
    } catch (Exception e) {
      log.error("An error occurred during update processing: {}", e.getMessage(), e);
    }
  }
}
