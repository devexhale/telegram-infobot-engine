package com.jawisimo.tbcfstarter.service.command;

import com.jawisimo.tbcfstarter.command.LastCommand;
import com.jawisimo.tbcfstarter.command.StartCommand;
import com.jawisimo.tbcfstarter.repository.DialogRepository;
import com.jawisimo.tbcfstarter.handler.NodeProcessor;
import com.jawisimo.tbcfstarter.service.state.UserStateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "telegram.bot", name = "enable-last-command", havingValue = "true")
@Slf4j
public class LastCommandService extends AbstractNodeCommandService {
    private final LastCommand lastCommand;

    public LastCommandService(
            StartCommand startCommand,
            LastCommand lastCommand,
            DialogRepository dialogRepository,
            @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
            UserStateService userStateService,
            NodeProcessor nodeProcessor) {
        super(startCommand, dialogRepository, userStateService, nodeProcessor); // передаємо startCommand сюди
        this.lastCommand = lastCommand;
    }


    @Override
    public String getCommandKey() {
        return lastCommand.getCommandName();
    }

    @Override
    protected String resolveNodeKey(String chatId) {
        return getUserStateService().getUserStateOrDefault(chatId, getStartCommand().getCommandName());
    }

}
