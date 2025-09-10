package com.jawisimo.tbcfstarter.handler.media;

import com.jawisimo.tbcfstarter.model.ContentNode;
import com.jawisimo.tbcfstarter.support.MediaFileLoader;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class PhotoMediaHandler extends AbstractMediaHandler {

    public PhotoMediaHandler(TelegramClient client, MediaFileLoader mediaFileResolver) {
        super(client, mediaFileResolver);
    }

    @Override
    protected String getMediaType() {
        return "PHOTO";
    }

    @Override
    protected Message execute(ContentNode contentNode, String chatId) throws TelegramApiException {
        SendPhoto request = SendPhoto.builder()
                .chatId(chatId)
                .photo(getMediaFileLoader().loadMedia(contentNode.getMedia()))
                .caption(contentNode.getMedia().getCaption())
                .build();
        return getTelegramClient().execute(request);
    }

}
