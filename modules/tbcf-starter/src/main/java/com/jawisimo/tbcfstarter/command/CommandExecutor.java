package com.jawisimo.tbcfstarter.command;

import com.jawisimo.tbcfstarter.command.handler.CommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommandExecutor {
    private final List<CommandHandler> commandHandlers;

    public boolean executeIfExists(String chatId, String userInput) {
        if (userInput == null) return false;

        for (CommandHandler handler : commandHandlers) {
            if (userInput.equals(handler.getCommandKey())) {
                handler.handle(chatId);
                return true;
            }
        }

        return false;
    }
}
