package com.jawisimo.tbcfstarter.command.handler;

import com.jawisimo.tbcfstarter.annotation.UserStatePersistent;
import com.jawisimo.tbcfstarter.command.commandset.LastCommand;
import com.jawisimo.tbcfstarter.command.commandset.StartCommand;
import com.jawisimo.tbcfstarter.dialog.NodeExecutor;
import com.jawisimo.tbcfstarter.repository.DialogRepository;
import com.jawisimo.tbcfstarter.service.UserStateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@UserStatePersistent
@Slf4j
class LastCommandHandler extends AbstractCommandHandler {

    LastCommandHandler(DialogRepository dialogRepository, UserStateService userStateService, NodeExecutor nodeExecutor) {
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
