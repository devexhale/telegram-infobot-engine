package com.jawisimo.tbcfstarter.handler;

import com.jawisimo.tbcfstarter.model.DialogNode;
import com.jawisimo.tbcfstarter.repository.DialogRepository;
import com.jawisimo.tbcfstarter.service.UserStateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@Order(1)
@Slf4j
@RequiredArgsConstructor
public class TextHandler implements UpdateHandler {
    private final DialogRepository dialogRepository;
    private final UserStateService userStateService;
    private final TelegramClient client;

    @Override
    public boolean supports(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

    @Override
    public void handle(Update update)  {
        String chatId = update.getMessage().getChatId().toString();
        String currentState = userStateService.getUserStateOrDefault(chatId, "start");
        DialogNode node = dialogRepository.getDialogNode(currentState);
        String text = node.getText();
        SendMessage textMessage = new SendMessage(chatId, text);
        executeTextMessage(textMessage);
    }

    private void executeTextMessage(SendMessage sendMessage)  {
        try {
            client.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Telegram API Exception: {}", e.getMessage(), e);
        }
    }
}
