package com.jawisimo.tbcfstarter.dialog.command.commandset;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class StartCommand implements Command {
    public static final String COMMAND_NAME = "/start";
    private static final String COMMAND_DESCRIPTION = "Start a dialogue with the bot";

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public String getDescription() {
        return COMMAND_DESCRIPTION;
    }
}
