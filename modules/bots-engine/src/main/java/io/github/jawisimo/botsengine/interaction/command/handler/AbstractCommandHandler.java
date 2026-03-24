package io.github.jawisimo.botsengine.interaction.command.handler;

import io.github.jawisimo.botsengine.model.DialogNode;
import io.github.jawisimo.botsengine.interaction.navigator.NodeExecutor;
import io.github.jawisimo.botsengine.repository.DialogRepository;
import io.github.jawisimo.botsengine.service.SubscriberService;
import io.github.jawisimo.botsengine.service.UserStateService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Base implementation of {@link CommandHandler} providing common command logic.
 *
 * <p>Resolves the target dialog node, executes it using {@link NodeExecutor}, and persists the
 * resulting user state.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Getter(AccessLevel.PACKAGE)
@Slf4j
public abstract class AbstractCommandHandler implements CommandHandler {

  private final DialogRepository dialogRepository;
  private final SubscriberService subscriberService;
  private final UserStateService userStateService;
  private final NodeExecutor nodeExecutor;

  /**
   * Executes the command by resolving and running the associated dialog node.
   *
   * @param chatId the chat identifier
   */
  @Override
  public void handle(String chatId) {
    DialogNode node = dialogRepository.getNode(getNodeKey(chatId));

    if (node == null) {
      log.warn("Node '{}' not found for chat {}", getNodeKey(chatId), chatId);
      return;
    }

    nodeExecutor.execute(node, chatId);
    userStateService.saveUserState(chatId, getNodeKey(chatId));
  }

  /**
   * Returns the dialog node key associated with the command.
   *
   * @param chatId the chat identifier
   * @return the dialog node key
   */
  abstract String getNodeKey(String chatId);
}
