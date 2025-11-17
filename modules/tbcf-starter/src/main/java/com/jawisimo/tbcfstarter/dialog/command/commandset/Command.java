package com.jawisimo.tbcfstarter.dialog.command.commandset;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

public interface Command {

    default BotCommand getCommand() {
        return new BotCommand(getCommandName(), getDescription());
    }

    String getCommandName();

    String getDescription();
}
