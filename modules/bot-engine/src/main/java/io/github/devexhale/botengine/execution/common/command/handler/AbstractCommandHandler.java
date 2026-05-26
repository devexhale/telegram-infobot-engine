package io.github.devexhale.botengine.execution.common.command.handler;

import io.github.devexhale.botengine.domain.dialog.DialogNode;
import io.github.devexhale.botengine.execution.dialog.navigator.DialogNodeExecutor;
import io.github.devexhale.botengine.execution.dialog.navigator.DialogNodeNavigator;
import io.github.devexhale.botengine.service.UserStateService;
import io.github.devexhale.botengine.storage.definition.DefinitionStorage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Base implementation of {@link CommandHandler} providing common command logic.
 *
 * <p>Resolves the target dialog node, executes it using {@link DialogNodeExecutor}, and persists
 * the resulting user state.
 *
 * @since 1.0
 */
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractCommandHandler implements CommandHandler {

  private final DefinitionStorage<DialogNode> dialogStorage;

  @Getter(AccessLevel.PACKAGE)
  private final UserStateService userStateService;

  private final DialogNodeNavigator nodeNavigator;

  /**
   * Executes the command by resolving and running the associated dialog node.
   *
   * @param chatId the chat identifier
   */
  @Override
  public void handle(String chatId) {
    DialogNode node = dialogStorage.getNode(getNodeKey(chatId));

    if (node == null) {
      log.warn("Dialog node '{}' not found. ChatID={}", getNodeKey(chatId), chatId);
      return;
    }

    nodeNavigator.navigateMessage(chatId, getNodeKey(chatId));
  }

  /**
   * Returns the dialog node key associated with the command.
   *
   * @param chatId the chat identifier
   * @return the dialog node key
   */
  abstract String getNodeKey(String chatId);
}
