package com.jawisimo.tbcfstarter.handler.media;

import com.jawisimo.tbcfstarter.model.ContentNode;
import com.jawisimo.tbcfstarter.support.MediaFileLoader;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class AudioMediaHandler extends AbstractMediaHandler {

    public AudioMediaHandler(TelegramClient client, MediaFileLoader mediaFileLoader) {
        super(client, mediaFileLoader);
    }

    @Override
    protected String getMediaType() {
        return "AUDIO";
    }

    @Override
    protected Message execute(ContentNode contentNode, String chatId) throws TelegramApiException {
        SendAudio request = SendAudio.builder()
                .chatId(chatId)
                .audio(getMediaFileLoader().loadMedia(contentNode.getMedia()))
                .caption(contentNode.getMedia().getCaption())
                .build();
        return getTelegramClient().execute(request);
    }

}
