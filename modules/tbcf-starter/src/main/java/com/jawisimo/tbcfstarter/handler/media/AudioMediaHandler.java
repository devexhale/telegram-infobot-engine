package com.jawisimo.tbcfstarter.handler.media;

import com.jawisimo.tbcfstarter.model.ContentNode;
import com.jawisimo.tbcfstarter.loader.MediaFileLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@Slf4j
public class AudioMediaHandler extends AbstractMediaHandler {

    public AudioMediaHandler(TelegramClient client, MediaFileLoader mediaFileLoader) {
        super(client, mediaFileLoader);
    }

    @Override
    protected String getMediaType() {
        return "AUDIO";
    }

    @Override
    protected Message executeMedia(ContentNode contentNode, String chatId) {
        SendAudio request = SendAudio.builder()
                .chatId(chatId)
                .audio(getMediaFileLoader().loadMedia(contentNode.getMedia()))
                .caption(contentNode.getMedia().getCaption())
                .build();

        try {
            return getTelegramClient().execute(request);
        } catch (TelegramApiException e) {
            log.error("Failed to handle audio in chat: {}", chatId, e);
            return null;
        }
    }
}
