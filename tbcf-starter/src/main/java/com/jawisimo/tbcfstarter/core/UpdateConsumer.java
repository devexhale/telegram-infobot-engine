package com.jawisimo.tbcfstarter.core;

import com.jawisimo.tbcfstarter.command.CommandsInitializer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {
    private final CommandsInitializer commandsInitializer;
    private final TelegramClient client;

    @PostConstruct
    public void init() {
        commandsInitializer.setUpCommands();
    }

    @Override
    public void consume(Update update) {
        String sentMessage = "Hello";
        SendMessage sendMessage;

        if (update.hasMessage() && update.getMessage().hasText()) {
            if (update.getMessage().getText().startsWith("/start")) {
                sendMessage = new SendMessage(update.getMessage().getChatId().toString(), sentMessage);

                try {
                    client.execute(sendMessage);
                } catch (TelegramApiException e) {
                    log.error("Telegram API Exception: {}", e.getMessage());
                }
            }
        }
    }
}
