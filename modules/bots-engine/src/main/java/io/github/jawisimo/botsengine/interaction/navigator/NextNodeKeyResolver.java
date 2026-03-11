package io.github.jawisimo.botsengine.interaction.navigator;

import io.github.jawisimo.botsengine.interaction.command.commandset.StartCommand;
import io.github.jawisimo.botsengine.model.ButtonType;
import io.github.jawisimo.botsengine.model.DialogNode;
import io.github.jawisimo.botsengine.interaction.navigator.dto.NavigationRequest;
import io.github.jawisimo.botsengine.repository.DialogRepository;
import io.github.jawisimo.botsengine.service.UserStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Resolves the next dialog node key based on the current dialog state and user input.
 *
 * <p>Determines the navigation target by inspecting the current {@link DialogNode} and evaluating
 * the user's input. When the current node uses a {@link ButtonType#REPLY} keyboard, the resolver
 * attempts to match the input against available button labels and returns the corresponding
 * navigation target.
 *
 * <p>If no matching button is found, or the current node does not use a reply keyboard, the user
 * input is treated as a raw node key and returned as-is.
 *
 * <p>The current node is resolved using {@link UserStateService} and {@link DialogRepository}.
 *
 * <p>If no user state exists, the resolver falls back to {@link StartCommand#COMMAND_NAME}.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class NextNodeKeyResolver {

  private final UserStateService userStateService;
  private final DialogRepository dialogRepository;

  /**
   * Resolves the navigation request for the given chat and user input.
   *
   * <p>If the current node contains a {@link ButtonType#REPLY} keyboard, the input is matched
   * against button labels. When a match is found, the corresponding target node key is returned and
   * marked as originating from a button interaction.
   *
   * <p>If no match exists or the current node does not define reply buttons, the input is treated
   * as raw user input and returned unchanged as the target node key.
   *
   * @param chatId the chat identifier
   * @param userInput the raw input received from the user
   * @return a {@link NavigationRequest} containing the resolved node key and the input origin
   */
  public NavigationRequest resolve(String chatId, String userInput) {
    DialogNode currentNode = getCurrentNode(chatId);

    if (currentNode == null
        || currentNode.buttonType() != ButtonType.REPLY
        || currentNode.buttons() == null) {
      return new NavigationRequest(userInput, userInput, false);
    }

    return currentNode.buttons().stream()
        .filter(btn -> btn.label().equals(userInput))
        .map(btn -> new NavigationRequest(userInput, btn.next(), true))
        .findFirst()
        .orElseGet(() -> new NavigationRequest(userInput, userInput, false));
  }

  private DialogNode getCurrentNode(String chatId) {
    String currentNodeKey =
        userStateService.getUserStateOrDefault(chatId, StartCommand.COMMAND_NAME);

    return dialogRepository.getNode(currentNodeKey);
  }
}
