package com.jawisimo.tbcfstarter.interaction.media.handler;

import com.jawisimo.tbcfstarter.interaction.node.model.ContentNode;
import com.jawisimo.tbcfstarter.interaction.node.model.ContentType;
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
public class TextMediaHandler implements MediaHandler {
    private final TelegramClient client;

    @Override
    public boolean canHandle(ContentNode contentNode) {
        return contentNode.type()== ContentType.TEXT;
    }

    @Override
    public Message handle(ContentNode contentNode, String chatId)  {
        String text = contentNode.text();
        SendMessage sendMessage = new SendMessage(chatId, text);

        try {
            return client.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Failed to handle text in chat: {}", chatId, e);
            return null;
        }
    }
}
