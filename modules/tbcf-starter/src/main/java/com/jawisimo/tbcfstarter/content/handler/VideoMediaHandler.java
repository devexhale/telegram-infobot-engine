package com.jawisimo.tbcfstarter.content.handler;

import com.jawisimo.tbcfstarter.loader.MediaFileLoader;
import com.jawisimo.tbcfstarter.model.ContentNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@Slf4j
class VideoMediaHandler extends MediaHandler {

    public VideoMediaHandler(TelegramClient client, MediaFileLoader mediaFileLoader) {
        super(client, mediaFileLoader);
    }

    @Override
    String getMediaType() {
        return "VIDEO";
    }

    @Override
    public Message handle(ContentNode contentNode, String chatId) {
        SendVideo request = SendVideo.builder()
                .chatId(chatId)
                .video(getMediaFile(getMedia(contentNode)))
                .caption(getMedia(contentNode).getCaption())
                .build();

        try {
            return getClient().execute(request);
        } catch (TelegramApiException e) {
            log.error("Failed to handle video in chat: {}", chatId, e);
            return null;
        }
    }
}
