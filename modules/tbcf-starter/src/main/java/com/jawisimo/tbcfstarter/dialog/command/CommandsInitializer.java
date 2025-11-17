package com.jawisimo.tbcfstarter.dialog.command;

import com.jawisimo.tbcfstarter.dialog.command.commandset.Command;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CommandsInitializer {
    private final TelegramClient client;
    private final List<Command> commands;

    public void setUpCommands() {
        List<BotCommand> botCommands = commands.stream()
                .map(Command::getCommand)
                .toList();
        SetMyCommands setMyCommands = new SetMyCommands(botCommands);
        setMyCommands.setScope(new BotCommandScopeDefault());

        try {
            client.execute(setMyCommands);
            log.info("Bot commands successfully set: {}", botCommands);
        } catch (TelegramApiException e) {
            log.error("Failed to set bot commands: {}", e.getMessage());
        }
    }
}


