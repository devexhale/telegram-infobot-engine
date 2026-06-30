package io.github.devexhale.botengine.execution.dialog;

import io.github.devexhale.botengine.execution.common.command.CommandExecutor;
import io.github.devexhale.botengine.execution.dialog.navigator.DialogNodeNavigator;
import io.github.devexhale.botengine.execution.support.MessageCleanupManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;

/**
 * Entry point for processing dialog interactions and routing updates.
 *
 * <p>Handles incoming {@link Message} and {@link CallbackQuery} events, delegating to command
 * execution or dialog navigation.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DialogExecutor {

  private final MessageCleanupManager cleanupService;
  private final DialogNodeNavigator dialogNodeNavigator;
  private final CommandExecutor commandExecutor;

  /**
   * Processes an incoming text message update.
   *
   * @param message the incoming Telegram {@link Message}
   */
  public void executeMessage(Message message) {
    if (message == null || message.getChatId() == null || message.getMessageId() == null) {
      log.warn("Skipping message due to missing required fields");
      return;
    }

    cleanupService.cleanRedundantMessage(message);

    String chatId = message.getChatId().toString();
    String userInput = message.getText();

    if (!commandExecutor.executeIfExists(chatId, userInput)) {
      dialogNodeNavigator.navigateMessage(chatId, userInput);
    }
  }

  /**
   * Processes an incoming callback query event.
   *
   * @param callbackQuery the incoming Telegram {@link CallbackQuery}
   */
  public void executeCallback(CallbackQuery callbackQuery) {
    if (callbackQuery == null
        || callbackQuery.getMessage() == null
        || callbackQuery.getMessage().getChatId() == null) {
      log.warn("Skipping callback due to missing required fields");
      return;
    }

    String chatId = callbackQuery.getMessage().getChatId().toString();
    String callbackData = callbackQuery.getData();

    if (!commandExecutor.executeIfExists(chatId, callbackData)) {
      dialogNodeNavigator.navigateCallback(chatId, callbackData);
    }
  }
}
