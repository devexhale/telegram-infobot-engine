package com.jawisimo.tbcfstarter.command.handler;

import com.jawisimo.tbcfstarter.command.commandset.StartCommand;
import com.jawisimo.tbcfstarter.handler.NodeProcessor;
import com.jawisimo.tbcfstarter.repository.DialogRepository;
import com.jawisimo.tbcfstarter.service.UserStateService;
import org.springframework.stereotype.Component;

@Component
class StartCommandHandler extends AbstractCommandHandler {

    StartCommandHandler(DialogRepository dialogRepository, UserStateService userStateService, NodeProcessor nodeProcessor) {
        super(dialogRepository, userStateService, nodeProcessor);
    }

    @Override
    public String getCommandKey() {
        return StartCommand.COMMAND_NAME;
    }

    @Override
    public void handle(String chatId) {
        super.handle(chatId);
    }

    @Override
    String getNodeKey(String chatId) {
        return getCommandKey();
    }
}
