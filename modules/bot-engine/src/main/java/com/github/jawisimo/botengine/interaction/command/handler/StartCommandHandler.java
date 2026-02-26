package com.github.jawisimo.botengine.interaction.command.handler;

import com.github.jawisimo.botengine.interaction.command.commandset.StartCommand;
import com.github.jawisimo.botengine.interaction.node.NodeExecutor;
import com.github.jawisimo.botengine.repository.DialogRepository;
import com.github.jawisimo.botengine.service.UserStateService;
import org.springframework.stereotype.Component;

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
