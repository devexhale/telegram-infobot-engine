package com.jawisimo.tbcfstarter.handler.media;

import com.jawisimo.tbcfstarter.model.ContentNode;
import com.jawisimo.tbcfstarter.loader.MediaFileLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@Slf4j
public class VideoMediaHandler extends AbstractMediaHandler {

    public VideoMediaHandler(TelegramClient client, MediaFileLoader mediaFileResolver) {
        super(client, mediaFileResolver);
    }

    @Override
    protected String getMediaType() {
        return "VIDEO";
    }

    @Override
    protected Message executeMedia(ContentNode contentNode, String chatId) {
        SendVideo request = SendVideo.builder()
                .chatId(chatId)
                .video(getMediaFileLoader().loadMedia(contentNode.getMedia()))
                .caption(contentNode.getMedia().getCaption())
                .build();

        try {
            return getTelegramClient().execute(request);
        } catch (TelegramApiException e) {
            log.error("Failed to handle video in chat: {}", chatId, e);
            return null;
        }
    }
}
