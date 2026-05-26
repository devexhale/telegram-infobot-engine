package io.github.devexhale.botengine.execution.common.command.handler;

import io.github.devexhale.botengine.domain.dialog.DialogNode;
import io.github.devexhale.botengine.execution.common.command.commandset.StartCommand;
import io.github.devexhale.botengine.execution.dialog.navigator.DialogNodeNavigator;
import io.github.devexhale.botengine.service.UserStateService;
import io.github.devexhale.botengine.storage.definition.DefinitionStorage;
import org.springframework.stereotype.Component;

/**
 * {@link CommandHandler} implementation for the {@code /start} command.
 *
 * <p>Resolves the start node and triggers dialog execution from the beginning.
 *
 * @since 1.0
 */
@Component
public class StartCommandHandler extends AbstractCommandHandler {

  StartCommandHandler(
      DefinitionStorage<DialogNode> dialogStorage,
      UserStateService userStateService,
      DialogNodeNavigator nodeNavigator) {
    super(dialogStorage, userStateService, nodeNavigator);
  }

  @Override
  public String getCommandKey() {
    return StartCommand.COMMAND_NAME;
  }

  @Override
  String getNodeKey(String chatId) {
    return getCommandKey();
  }
}
