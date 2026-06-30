package io.github.devexhale.botengine.execution.dialog.navigator;

import io.github.devexhale.botengine.domain.dialog.ButtonType;
import io.github.devexhale.botengine.domain.dialog.DialogNode;
import io.github.devexhale.botengine.execution.common.command.commandset.StartCommand;
import io.github.devexhale.botengine.service.UserStateService;
import io.github.devexhale.botengine.storage.definition.DefinitionStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Resolves and executes dialog node transitions based on user input or callbacks.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DialogNodeNavigator {

  private static final String DELETE_MSG = "Message deleted from chat";

  private final DefinitionStorage<DialogNode> dialogStorage;
  private final DialogNodeExecutor dialogNodeExecutor;
  private final UserStateService userStateService;

  /**
   * Navigates to the next node based on a regular text message.
   *
   * <p>If the current node has a reply keyboard, matches the input against button labels.
   *
   * @param chatId the chat identifier
   * @param userInput the raw message text
   */
  public void navigateMessage(String chatId, String userInput) {
    DialogNode currentNode = getCurrentNode(chatId);

    if (currentNode == null
        || currentNode.buttonType() != ButtonType.REPLY
        || currentNode.buttons() == null) {
      navigate(chatId, userInput, userInput, false);
      return;
    }

    currentNode.buttons().stream()
        .filter(button -> button.label().equals(userInput))
        .findFirst()
        .ifPresentOrElse(
            button -> navigate(chatId, userInput, button.next(), true),
            () -> navigate(chatId, userInput, userInput, false));
  }

  /**
   * Navigates to the next node based on an inline keyboard callback query.
   *
   * @param chatId the chat identifier
   * @param callbackData the callback data representing the target node key
   */
  public void navigateCallback(String chatId, String callbackData) {
    navigate(chatId, callbackData, callbackData, true);
  }

  private void navigate(String chatId, String rawInput, String nodeId, boolean fromButton) {
    DialogNode targetNode = dialogStorage.getNode(nodeId);

    if (targetNode == null) {
      handleMissingNode(chatId, rawInput, nodeId, fromButton);
      return;
    }

    dialogNodeExecutor.execute(targetNode, chatId);
    userStateService.saveUserState(chatId, nodeId);
  }

  private void handleMissingNode(
      String chatId, String rawInput, String nodeId, boolean fromButton) {
    if (fromButton) {
      log.warn("Dialog node '{}' not found. ChatID={}", nodeId, chatId);
      return;
    }

    log.warn("Irrelevant message sent: '{}'. {}. ChatID={}", rawInput, DELETE_MSG, chatId);
  }

  private DialogNode getCurrentNode(String chatId) {
    String currentNodeId =
        userStateService.getUserStateOrDefault(chatId, StartCommand.COMMAND_NAME);

    return dialogStorage.getNode(currentNodeId);
  }
}
