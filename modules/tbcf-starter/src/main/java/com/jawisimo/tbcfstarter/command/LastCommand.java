package com.jawisimo.tbcfstarter.command;

import com.jawisimo.tbcfstarter.annotation.UserStatePersistent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

@Component
@UserStatePersistent
@Order(2)
public class LastCommand implements Command {
    public static final String COMMAND_NAME = "/last";
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
