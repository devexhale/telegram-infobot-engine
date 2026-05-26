package io.github.devexhale.botengine.execution.common.command.handler;

import io.github.devexhale.botengine.domain.dialog.DialogNode;
import io.github.devexhale.botengine.execution.common.command.commandset.LastCommand;
import io.github.devexhale.botengine.execution.common.command.commandset.StartCommand;
import io.github.devexhale.botengine.execution.dialog.navigator.DialogNodeNavigator;
import io.github.devexhale.botengine.service.UserStateService;
import io.github.devexhale.botengine.storage.definition.DefinitionStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * {@link CommandHandler} implementation for the {@code /last} command.
 *
 * <p>Restores the user's last saved dialog position when persistent state is enabled.
 *
 * @since 1.0
 */
@Component
@Slf4j
public class LastCommandHandler extends AbstractCommandHandler {

  LastCommandHandler(
      DefinitionStorage<DialogNode> dialogStorage,
      UserStateService userStateService,
      DialogNodeNavigator nodeNavigator) {
    super(dialogStorage, userStateService, nodeNavigator);
  }

  @Override
  public String getCommandKey() {
    return LastCommand.COMMAND_NAME;
  }

  @Override
  String getNodeKey(String chatId) {
    return getUserStateService().getUserStateOrDefault(chatId, StartCommand.COMMAND_NAME);
  }
}
