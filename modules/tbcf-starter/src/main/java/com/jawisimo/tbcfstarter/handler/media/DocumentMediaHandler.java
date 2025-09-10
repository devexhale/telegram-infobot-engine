package com.jawisimo.tbcfstarter.handler.media;

import com.jawisimo.tbcfstarter.model.ContentNode;
import com.jawisimo.tbcfstarter.support.MediaFileLoader;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

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
    protected Message execute(ContentNode contentNode, String chatId) throws TelegramApiException {
        SendDocument request = SendDocument.builder()
                .chatId(chatId)
                .document(getMediaFileLoader().loadMedia(contentNode.getMedia()))
                .caption(contentNode.getMedia().getCaption())
                .build();
        return getTelegramClient().execute(request);
    }

}
