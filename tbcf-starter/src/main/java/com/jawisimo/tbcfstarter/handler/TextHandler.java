package com.jawisimo.tbcfstarter.handler;

import com.jawisimo.tbcfstarter.model.DialogNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@Order(1)
@Slf4j
@RequiredArgsConstructor
public class TextHandler implements UpdateHandler {
    private final TelegramClient client;

    @Override
    public boolean supports(DialogNode node) {
        return node.getText() != null;
    }

    @Override
    public void handle(DialogNode node, String chatId)  {
        String text = node.getText();
        SendMessage sendMessage = new SendMessage(chatId, text);
        executeTextMessage(sendMessage);
    }

    private void executeTextMessage(SendMessage sendMessage)  {
        try {
            client.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Telegram API Exception: {}", e.getMessage(), e);
        }
    }
}
