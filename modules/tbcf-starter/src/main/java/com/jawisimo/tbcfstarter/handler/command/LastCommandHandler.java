package com.jawisimo.tbcfstarter.handler.command;

import com.jawisimo.tbcfstarter.annotation.UserStatePersistent;
import com.jawisimo.tbcfstarter.command.LastCommand;
import com.jawisimo.tbcfstarter.command.StartCommand;
import com.jawisimo.tbcfstarter.handler.NodeProcessor;
import com.jawisimo.tbcfstarter.repository.DialogRepository;
import com.jawisimo.tbcfstarter.service.UserStateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@UserStatePersistent
@Slf4j
class LastCommandHandler extends AbstractCommandHandler {

    public LastCommandHandler(DialogRepository dialogRepository, UserStateService userStateService, NodeProcessor nodeProcessor) {
        super(dialogRepository, userStateService, nodeProcessor);
    }

    @Override
    public String getCommandKey() {
        return LastCommand.COMMAND_NAME;
    }

    @Override
    public void handle(String chatId) {
        super.handle(chatId);
    }

    @Override
    String getNodeKey(String chatId) {
        return getUserStateService().getUserStateOrDefault(chatId, StartCommand.COMMAND_NAME);
    }
}
