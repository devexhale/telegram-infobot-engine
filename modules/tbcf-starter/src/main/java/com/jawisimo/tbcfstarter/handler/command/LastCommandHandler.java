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
public class LastCommandHandler extends AbstractCommandHandler {

    public LastCommandHandler(
            LastCommand lastCommand,
            DialogRepository dialogRepository,
            UserStateService userStateService,
            NodeProcessor nodeProcessor) {
        super(lastCommand, dialogRepository, userStateService, nodeProcessor);
    }

    @Override
    protected String resolveNodeKey(String chatId) {
        return getUserStateService().getUserStateOrDefault(chatId, StartCommand.COMMAND_NAME);
    }
}
