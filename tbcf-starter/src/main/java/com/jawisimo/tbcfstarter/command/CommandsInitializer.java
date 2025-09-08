package com.jawisimo.tbcfstarter.command;

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
        List<BotCommand> telegramCommands = commands.stream()
                .map(Command::getCommand)
                .toList();
        SetMyCommands setMyCommands = new SetMyCommands(telegramCommands);
        setMyCommands.setScope(new BotCommandScopeDefault());

        try {
            client.execute(setMyCommands);
            log.info("Bot commands successfully set: {}", telegramCommands);
        } catch (TelegramApiException e) {
            log.error("Failed to set bot commands: {}", e.getMessage());
        }
    }

}
