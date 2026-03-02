package com.github.jawisimo.botengine.interaction.node;

import com.github.jawisimo.botengine.interaction.command.commandset.StartCommand;
import com.github.jawisimo.botengine.interaction.node.model.Button;
import com.github.jawisimo.botengine.interaction.node.model.ButtonType;
import com.github.jawisimo.botengine.interaction.node.model.DialogNode;
import com.github.jawisimo.botengine.repository.DialogRepository;
import com.github.jawisimo.botengine.service.UserStateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Resolves and navigates between dialog nodes during execution.
 *
 * <p>Determines the next node based on user input. Delegates execution to {@link NodeExecutor}.
 *
 * <p>Uses {@link UserStateService} to resolve the current dialog position.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NodeNavigator {

  private final UserStateService userStateService;
  private final DialogRepository dialogRepository;
  private final NodeExecutor nodeExecutor;

  /**
   * Resolves the next dialog node key based on the current node and user input.
   *
   * @param chatId the chat identifier
   * @param userInput the user input or callback data
   * @return the resolved next node identifier
   */
  public String getNextNodeKey(String chatId, String userInput) {
    DialogNode currentNode = getCurrentNode(chatId);

    if (currentNode != null
        && currentNode.buttonType() == ButtonType.REPLY
        && currentNode.buttons() != null) {
      return currentNode.buttons().stream()
          .filter(btn -> btn.label().equals(userInput))
          .map(Button::next)
          .findFirst()
          .orElse(userInput);
    }

    return userInput;
  }

  /**
   * Navigates to the specified dialog node and executes it.
   *
   * @param chatId the chat identifier
   * @param nodeKey the target dialog node identifier
   * @return {@code true} if navigation succeeded, {@code false} otherwise
   */
  public boolean navigateToNode(String chatId, String nodeKey) {
    if (nodeKey == null) {
      return false;
    }

    DialogNode node = dialogRepository.getNode(nodeKey);

    if (node == null) {
      return false;
    }

    nodeExecutor.execute(node, chatId);
    return true;
  }

  private DialogNode getCurrentNode(String chatId) {
    String currentNodeKey =
        userStateService.getUserStateOrDefault(chatId, StartCommand.COMMAND_NAME);

    return dialogRepository.getNode(currentNodeKey);
  }
}
