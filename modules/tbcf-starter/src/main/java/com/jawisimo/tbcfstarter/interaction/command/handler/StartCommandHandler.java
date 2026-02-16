package com.jawisimo.tbcfstarter.interaction.command.handler;

import com.jawisimo.tbcfstarter.interaction.command.commandset.StartCommand;
import com.jawisimo.tbcfstarter.interaction.node.NodeExecutor;
import com.jawisimo.tbcfstarter.repository.DialogRepository;
import com.jawisimo.tbcfstarter.service.UserStateService;
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
