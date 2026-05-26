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
 * Resolves and executes dialog node transitions.
 *
 * <p>Handles both message-based and callback-based navigation:
 *
 * <ul>
 *   <li>For regular messages, resolves the next node using the current dialog state.
 *   <li>For callback queries, treats callback data as the next node key directly.
 * </ul>
 *
 * <p>If the resolved node exists, delegates execution to {@link DialogNodeExecutor} and persists
 * the new current node in {@link UserStateService}.
 *
 * <p>If the node does not exist, logs either a missing-node warning for button-based navigation or
 * an irrelevant-input warning for raw user input.
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
   * Resolves and executes navigation for a regular message.
   *
   * <p>If the current node uses a {@link ButtonType#REPLY} keyboard, the input is matched against
   * button labels and the corresponding {@code next} node key is used. Otherwise, the input is
   * treated as a raw node key.
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
   * Resolves and executes navigation for a callback query.
   *
   * <p>Callback data is treated as the target node key directly.
   *
   * @param chatId the chat identifier
   * @param callbackData the callback data
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
