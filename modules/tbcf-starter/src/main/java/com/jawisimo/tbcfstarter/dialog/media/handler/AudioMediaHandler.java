package com.jawisimo.tbcfstarter.dialog.media.handler;

import com.jawisimo.tbcfstarter.loader.MediaFileLoader;
import com.jawisimo.tbcfstarter.dialog.node.model.ContentNode;
import com.jawisimo.tbcfstarter.dialog.node.model.Media;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@Slf4j
public class AudioMediaHandler extends AbstractMediaHandler {

    AudioMediaHandler(TelegramClient client, MediaFileLoader mediaFileLoader) {
        super(client, mediaFileLoader);
    }

    @Override
    public Message handle(ContentNode contentNode, String chatId) {
        Media media = getMedia(contentNode);
        SendAudio request = SendAudio.builder()
                .chatId(chatId)
                .audio(getMediaFile(media))
                .caption(media.getCaption())
                .build();

        try {
            return getClient().execute(request);
        } catch (TelegramApiException e) {
            log.error("Failed to handle audio in chat: {}", chatId, e);
            return null;
        }
    }

    @Override
    String getMediaType() {
        return "AUDIO";
    }
}
