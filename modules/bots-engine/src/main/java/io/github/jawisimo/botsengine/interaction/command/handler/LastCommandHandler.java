package io.github.jawisimo.botsengine.interaction.command.handler;

import io.github.jawisimo.botsengine.config.redis.UserStatePersistent;
import io.github.jawisimo.botsengine.interaction.command.commandset.LastCommand;
import io.github.jawisimo.botsengine.interaction.command.commandset.StartCommand;
import io.github.jawisimo.botsengine.interaction.navigator.NodeExecutor;
import io.github.jawisimo.botsengine.repository.DialogRepository;
import io.github.jawisimo.botsengine.service.SubscriberService;
import io.github.jawisimo.botsengine.service.UserStateService;
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
@UserStatePersistent
@Slf4j
public class LastCommandHandler extends AbstractCommandHandler {

  LastCommandHandler(
      DialogRepository dialogRepository,
      SubscriberService subscriberService,
      UserStateService userStateService,
      NodeExecutor nodeExecutor) {
    super(dialogRepository, subscriberService, userStateService, nodeExecutor);
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
