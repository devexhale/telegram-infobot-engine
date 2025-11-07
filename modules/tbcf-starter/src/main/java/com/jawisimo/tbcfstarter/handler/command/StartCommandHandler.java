package com.jawisimo.tbcfstarter.handler.command;

import com.jawisimo.tbcfstarter.command.StartCommand;
import com.jawisimo.tbcfstarter.handler.NodeProcessor;
import com.jawisimo.tbcfstarter.repository.DialogRepository;
import com.jawisimo.tbcfstarter.service.UserStateService;
import org.springframework.stereotype.Component;

@Component
public class StartCommandHandler extends AbstractCommandHandler {

    public StartCommandHandler(StartCommand startCommand,
                               DialogRepository dialogRepository,
                               UserStateService userStateService,
                               NodeProcessor nodeProcessor) {
        super(startCommand, dialogRepository, userStateService, nodeProcessor);
    }

    @Override
    protected String resolveNodeKey(String chatId) {
        return StartCommand.COMMAND_NAME;
    }
}
