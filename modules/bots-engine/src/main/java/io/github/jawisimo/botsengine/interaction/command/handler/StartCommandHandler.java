package io.github.jawisimo.botsengine.interaction.command.handler;

import io.github.jawisimo.botsengine.interaction.command.commandset.StartCommand;
import io.github.jawisimo.botsengine.interaction.navigator.NodeExecutor;
import io.github.jawisimo.botsengine.repository.DialogRepository;
import io.github.jawisimo.botsengine.service.UserStateService;
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
      DialogRepository dialogRepository,
      UserStateService userStateService,
      NodeExecutor nodeExecutor) {
    super(dialogRepository, userStateService, nodeExecutor);
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
