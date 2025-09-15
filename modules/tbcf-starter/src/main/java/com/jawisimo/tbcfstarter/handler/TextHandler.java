package com.jawisimo.tbcfstarter.handler;

import com.jawisimo.tbcfstarter.model.ContentNode;
import com.jawisimo.tbcfstarter.model.ContentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@Slf4j
@RequiredArgsConstructor
public class TextHandler implements ContentHandler  {
    private final TelegramClient client;


    @Override
    public boolean supports(ContentNode contentNode) {
        return contentNode.getType() == ContentType.TEXT;
    }

    @Override
    public Message handle(ContentNode contentNode, String chatId)  {
        String text = contentNode.getText();
        SendMessage sendMessage = new SendMessage(chatId, text);
        try {
            return client.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Telegram API Exception: {}", e.getMessage(), e);
            return null;
        }
    }

}
