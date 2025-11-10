package com.jawisimo.tbcfstarter.handler.command;

import com.jawisimo.tbcfstarter.command.StartCommand;
import com.jawisimo.tbcfstarter.handler.NodeProcessor;
import com.jawisimo.tbcfstarter.repository.CaffeineMessageRepository;
import com.jawisimo.tbcfstarter.service.UserStateService;
import org.springframework.stereotype.Component;

@Component
public class StartCommandHandler extends AbstractCommandHandler {

    public StartCommandHandler(StartCommand startCommand,
                               CaffeineMessageRepository caffeineMessageRepository,
                               UserStateService userStateService,
                               NodeProcessor nodeProcessor) {
        super(startCommand, caffeineMessageRepository, userStateService, nodeProcessor);
    }

    @Override
    protected String resolveNodeKey(String chatId) {
        return StartCommand.COMMAND_NAME;
    }
}
