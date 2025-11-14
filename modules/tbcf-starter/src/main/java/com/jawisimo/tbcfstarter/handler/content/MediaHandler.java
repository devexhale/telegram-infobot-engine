package com.jawisimo.tbcfstarter.handler.content;

import com.jawisimo.tbcfstarter.loader.MediaFileLoader;
import com.jawisimo.tbcfstarter.model.ContentNode;
import com.jawisimo.tbcfstarter.model.ContentType;
import com.jawisimo.tbcfstarter.model.Media;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@RequiredArgsConstructor
abstract class MediaHandler implements ContentHandler {
    @Getter
    private final TelegramClient client;
    private final MediaFileLoader mediaFileLoader;

    InputFile getMediaFile(Media media) {
        return mediaFileLoader.loadMedia(media);
    }

    abstract String getMediaType();

    @Override
    public boolean canHandle(ContentNode contentNode) {
        if (contentNode.getType() != ContentType.MEDIA) return false;
        return contentNode.getMedia() != null
                && contentNode.getMedia().getType().equalsIgnoreCase(getMediaType());
    }

    @Override
    public abstract Message handle(ContentNode contentNode, String chatId);

    Media getMedia(ContentNode contentNode) {
        return contentNode.getMedia();
    }

}
