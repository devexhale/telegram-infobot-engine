package com.github.jawisimo.tbcfstarter.interaction.command.handler;

import com.github.jawisimo.tbcfstarter.annotation.UserStatePersistent;
import com.github.jawisimo.tbcfstarter.interaction.command.commandset.LastCommand;
import com.github.jawisimo.tbcfstarter.interaction.command.commandset.StartCommand;
import com.github.jawisimo.tbcfstarter.interaction.node.NodeExecutor;
import com.github.jawisimo.tbcfstarter.repository.DialogRepository;
import com.github.jawisimo.tbcfstarter.service.UserStateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@UserStatePersistent
@Slf4j
public class LastCommandHandler extends AbstractCommandHandler {

  LastCommandHandler(
      DialogRepository dialogRepository,
      UserStateService userStateService,
      NodeExecutor nodeExecutor) {
    super(dialogRepository, userStateService, nodeExecutor);
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
