package com.jawisimo.tbcfstarter.dialog.media.handler;

import com.jawisimo.tbcfstarter.loader.MediaFileLoader;
import com.jawisimo.tbcfstarter.dialog.node.model.ContentNode;
import com.jawisimo.tbcfstarter.dialog.node.model.ContentType;
import com.jawisimo.tbcfstarter.dialog.node.model.Media;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@RequiredArgsConstructor
public abstract class AbstractMediaHandler implements MediaHandler {

    @Getter(AccessLevel.PACKAGE)
    private final TelegramClient client;
    private final MediaFileLoader mediaFileLoader;

    @Override
    public boolean canHandle(ContentNode contentNode) {
        if (contentNode.getType() != ContentType.MEDIA) return false;
        return contentNode.getMedia() != null
                && contentNode.getMedia().getType().equalsIgnoreCase(getMediaType());
    }

    @Override
    public abstract Message handle(ContentNode contentNode, String chatId);

    final InputFile getMediaFile(Media media) {
        return mediaFileLoader.loadMedia(media);
    }

    final Media getMedia(ContentNode contentNode) {
        return contentNode.getMedia();
    }

    abstract String getMediaType();
}
