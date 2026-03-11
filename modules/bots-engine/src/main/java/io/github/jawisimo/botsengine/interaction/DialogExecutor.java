package io.github.jawisimo.botsengine.interaction;

import io.github.jawisimo.botsengine.interaction.command.CommandExecutor;
import io.github.jawisimo.botsengine.interaction.navigator.NavigationResultHandler;
import io.github.jawisimo.botsengine.interaction.navigator.NextNodeKeyResolver;
import io.github.jawisimo.botsengine.interaction.navigator.NodeRouter;
import io.github.jawisimo.botsengine.interaction.navigator.dto.NavigationRequest;
import io.github.jawisimo.botsengine.interaction.navigator.dto.NavigationResult;
import io.github.jawisimo.botsengine.service.MessageCleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;

/**
 * Entry point for executing dialog interactions within the framework.
 *
 * <p>Routes incoming {@link Message} updates and {@link CallbackQuery} events to command execution
 * or dialog navigation.
 *
 * <p>{@link MessageCleanupService} removes redundant user messages from the chat to keep the
 * conversation clean.
 *
 * <p>{@link CommandExecutor} handles commands when present. Otherwise, the input is resolved by
 * {@link NextNodeKeyResolver} and routed by {@link NodeRouter}.
 *
 * <p>{@link NavigationResultHandler} processes the {@link NavigationResult}.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DialogExecutor {

  private final MessageCleanupService cleanupService;
  private final NextNodeKeyResolver nextNodeKeyResolver;
  private final NodeRouter nodeRouter;
  private final NavigationResultHandler navigationResultHandler;
  private final CommandExecutor commandExecutor;

  /**
   * Processes an incoming message update.
   *
   * <p>Deletes redundant user messages and attempts to execute a command.
   *
   * <p>If no command matches, the message text is resolved to a {@link NavigationRequest}. The
   * request is routed to a dialog node and the navigation result is handled.
   *
   * @param message the incoming Telegram message
   */
  public void executeMessage(Message message) {
    cleanupService.deleteRedundantMessage(message);

    String chatId = message.getChatId().toString();
    String userInput = message.getText();

    if (commandExecutor.executeIfExists(chatId, userInput)) {
      return;
    }

    NavigationRequest request = nextNodeKeyResolver.resolve(chatId, userInput);
    NavigationResult result = nodeRouter.route(chatId, request);
    navigationResultHandler.handle(chatId, request, result);
  }

  /**
   * Processes an incoming callback query event.
   *
   * <p>Clears the last node message and attempts to execute a command.
   *
   * <p>If no command matches, callback data is treated as a button action.
   *
   * <p>The data is converted to a {@link NavigationRequest}.The request is routed and the
   * navigation result is handled.
   *
   * @param callbackQuery the incoming Telegram callback query
   */
  public void executeCallback(CallbackQuery callbackQuery) {
    String chatId = callbackQuery.getMessage().getChatId().toString();
    String callbackData = callbackQuery.getData();

    if (commandExecutor.executeIfExists(chatId, callbackData)) {
      return;
    }

    NavigationRequest request = new NavigationRequest(callbackData, callbackData, true);
    NavigationResult result = nodeRouter.route(chatId, request);
    navigationResultHandler.handle(chatId, request, result);
  }
}
