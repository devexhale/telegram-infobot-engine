package com.jawisimo.tbcfstarter.service.command;

import com.jawisimo.tbcfstarter.annotation.UserStatePersistent;
import com.jawisimo.tbcfstarter.command.LastCommand;
import com.jawisimo.tbcfstarter.command.StartCommand;
import com.jawisimo.tbcfstarter.handler.NodeProcessor;
import com.jawisimo.tbcfstarter.repository.DialogRepository;
import com.jawisimo.tbcfstarter.service.UserStateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@UserStatePersistent
@Slf4j
public class LastCommandService extends AbstractCommandService {

    public LastCommandService(
            LastCommand lastCommand,
            DialogRepository dialogRepository,
            UserStateService userStateService,
            NodeProcessor nodeProcessor) {
        super(lastCommand, dialogRepository, userStateService, nodeProcessor);
    }


    @Override
    public String getCommandKey() {
        return getCommand().getCommandName();
    }

    @Override
    protected String resolveNodeKey(String chatId) {
        return getUserStateService().getUserStateOrDefault(chatId, StartCommand.COMMAND_NAME);
    }

}
