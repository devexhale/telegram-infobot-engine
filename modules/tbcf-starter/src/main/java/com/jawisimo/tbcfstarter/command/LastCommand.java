package com.jawisimo.tbcfstarter.command;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

@Component
@ConditionalOnProperty(prefix = "telegram.bot", name = "enable-last-command", havingValue = "true")
@Order(2)
public class LastCommand implements Command {
    private static final String COMMAND_NAME = "/last";
    private static final String COMMAND_DESCRIPTION = "Return to where you left off";

    @Override
    public BotCommand getCommand() {
        return new BotCommand(COMMAND_NAME, COMMAND_DESCRIPTION);
    }

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

}
