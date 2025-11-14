package com.jawisimo.tbcfstarter.handler.content;

import com.jawisimo.tbcfstarter.loader.MediaFileLoader;
import com.jawisimo.tbcfstarter.model.ContentNode;
import com.jawisimo.tbcfstarter.model.Media;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@Slf4j
class PhotoMediaHandler extends MediaHandler {

    PhotoMediaHandler(TelegramClient client, MediaFileLoader mediaFileLoader) {
        super(client, mediaFileLoader);
    }

    @Override
    String getMediaType() {
        return "PHOTO";
    }

    @Override
    public Message handle(ContentNode contentNode, String chatId) {
        Media media = contentNode.getMedia();
        SendPhoto request = SendPhoto.builder()
                .chatId(chatId)
                .photo(getMediaFile(media))
                .caption(media.getCaption())
                .build();

        try {
            return getClient().execute(request);
        } catch (TelegramApiException e) {
            log.error("Failed to photo in chat: {}", chatId, e);
            return null;
        }
    }
}
