package com.jawisimo.tbcfstarter.handler.media;

import com.jawisimo.tbcfstarter.handler.ContentHandler;
import com.jawisimo.tbcfstarter.loader.MediaFileLoader;
import com.jawisimo.tbcfstarter.model.ContentNode;
import com.jawisimo.tbcfstarter.model.ContentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractMediaHandler implements ContentHandler {
    private final TelegramClient client;
    private final MediaFileLoader mediaFileLoader;

    protected TelegramClient getTelegramClient() {
        return client;
    }

    protected MediaFileLoader getMediaFileLoader() {
        return mediaFileLoader;
    }

    protected abstract String getMediaType();

    protected abstract Message executeMedia(ContentNode contentNode, String chatId);

    @Override
    public boolean supports(ContentNode contentNode) {
        if (contentNode.getType() != ContentType.MEDIA) return false;
        return contentNode.getMedia() != null && contentNode.getMedia().getType().equalsIgnoreCase(getMediaType());
    }

    @Override
    public Message handle(ContentNode contentNode, String chatId) {
        return executeMedia(contentNode, chatId);
    }
}
