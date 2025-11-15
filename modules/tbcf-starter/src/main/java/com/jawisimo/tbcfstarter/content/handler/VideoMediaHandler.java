package com.jawisimo.tbcfstarter.content.handler;

import com.jawisimo.tbcfstarter.loader.MediaFileLoader;
import com.jawisimo.tbcfstarter.model.ContentNode;
import com.jawisimo.tbcfstarter.model.Media;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@Slf4j
class VideoMediaHandler extends MediaHandler {

    VideoMediaHandler(TelegramClient client, MediaFileLoader mediaFileLoader) {
        super(client, mediaFileLoader);
    }

    @Override
    String getMediaType() {
        return "VIDEO";
    }

    @Override
    public Message handle(ContentNode contentNode, String chatId) {
        Media media = getMedia(contentNode);
        SendVideo request = SendVideo.builder()
                .chatId(chatId)
                .video(getMediaFile(media))
                .caption(media.getCaption())
                .build();

        try {
            return getClient().execute(request);
        } catch (TelegramApiException e) {
            log.error("Failed to handle video in chat: {}", chatId, e);
            return null;
        }
    }
}
