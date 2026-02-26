package com.github.jawisimo.botengine.interaction;

import com.github.jawisimo.botengine.interaction.command.CommandExecutor;
import com.github.jawisimo.botengine.interaction.node.NodeNavigator;
import com.github.jawisimo.botengine.service.MessageCleanupService;
import com.github.jawisimo.botengine.service.UserStateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Component
@RequiredArgsConstructor
@Slf4j
public class DialogExecutor {
  private final MessageCleanupService cleanupService;
  private final UserStateService userStateService;
  private final NodeNavigator nodeNavigator;
  private final CommandExecutor commandExecutor;

  private static final String DELETE_MESSAGE = "Message deleted from chat";

  public void executeMessage(Message message) {
    cleanupService.deleteRedundantMessage(message);

    String chatId = message.getChatId().toString();
    String userInput = message.getText();

    if (commandExecutor.executeIfExists(chatId, userInput)) return;

    String nextNodeKey = nodeNavigator.getNextNodeKey(chatId, userInput);

    if (nodeNavigator.navigateToNode(chatId, nextNodeKey)) {
      userStateService.saveUserState(chatId, nextNodeKey);
    } else {
      log.warn("Irrelevant message sent: \"{}\". {}: {}", userInput, DELETE_MESSAGE, chatId);
    }
  }

  public void executeCallback(CallbackQuery callbackQuery) {
    String chatId = callbackQuery.getMessage().getChatId().toString();
    String callbackData = callbackQuery.getData();

    cleanupService.clearLastNode(chatId);

    if (commandExecutor.executeIfExists(chatId, callbackData)) return;

    if (nodeNavigator.navigateToNode(chatId, callbackData)) {
      userStateService.saveUserState(chatId, callbackData);
    } else {
      log.warn(
          "No dialog node found for input: \"{}\". {}: {}", callbackData, DELETE_MESSAGE, chatId);
    }
  }
}
