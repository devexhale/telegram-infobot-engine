package com.jawisimo.tbcfstarter.command;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

@Component
@Order(1)
public class StartCommand implements Command {
    private static final String COMMAND_NAME = "/start";
    private static final String COMMAND_DESCRIPTION = "Start a dialogue with the bot";

    @Override
    public BotCommand getCommand() {
        return new BotCommand(COMMAND_NAME, COMMAND_DESCRIPTION);
    }

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

}
