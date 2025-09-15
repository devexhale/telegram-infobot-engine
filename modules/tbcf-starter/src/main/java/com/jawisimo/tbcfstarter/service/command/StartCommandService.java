package com.jawisimo.tbcfstarter.service.command;

import com.jawisimo.tbcfstarter.command.StartCommand;
import com.jawisimo.tbcfstarter.handler.NodeHandler;
import com.jawisimo.tbcfstarter.repository.DialogRepository;
import com.jawisimo.tbcfstarter.service.state.UserStateService;
import org.springframework.stereotype.Service;

@Service
public class StartCommandService extends AbstractNodeCommandService {

    public StartCommandService(StartCommand startCommand,
                               NodeHandler nodeHandler,
                               DialogRepository dialogRepository,
                               @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
                               UserStateService userStateService) {
        super(startCommand, nodeHandler, dialogRepository, userStateService);
    }

    @Override
    public String getCommandKey() {
        return getStartCommand().getCommandName();
    }

    @Override
    protected String resolveNodeKey(String chatId) {
        return getCommandKey();
    }

}
