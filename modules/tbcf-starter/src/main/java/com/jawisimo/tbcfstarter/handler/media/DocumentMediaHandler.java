package com.jawisimo.tbcfstarter.handler.media;

import com.jawisimo.tbcfstarter.model.ContentNode;
import com.jawisimo.tbcfstarter.loader.MediaFileLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Component
public class DocumentMediaHandler extends AbstractMediaHandler {

    public DocumentMediaHandler(TelegramClient client, MediaFileLoader mediaFileResolver) {
        super(client, mediaFileResolver);
    }

    @Override
    protected String getMediaType() {
        return "DOCUMENT";
    }

    @Override
    protected Message executeMedia(ContentNode contentNode, String chatId) {
        SendDocument request = SendDocument.builder()
                .chatId(chatId)
                .document(getMediaFileLoader().loadMedia(contentNode.getMedia()))
                .caption(contentNode.getMedia().getCaption())
                .build();

        try {
            return getTelegramClient().execute(request);
        } catch (TelegramApiException e) {
            log.error("Failed to handle document in chat: {}", chatId, e);
            return null;
        }
    }
}
