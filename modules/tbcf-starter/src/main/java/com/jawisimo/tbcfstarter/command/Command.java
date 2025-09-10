package com.jawisimo.tbcfstarter.command;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

public interface Command {
    BotCommand getCommand();
    String getCommandName();
}
