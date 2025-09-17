package com.jawisimo.tbcfstarter.handler.media;

import com.jawisimo.tbcfstarter.handler.ContentHandler;
import com.jawisimo.tbcfstarter.model.ContentNode;
import com.jawisimo.tbcfstarter.model.ContentType;
import com.jawisimo.tbcfstarter.support.MediaFileLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
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

    /**
     * Повертає надіслане повідомлення
     */
    protected abstract Message execute(ContentNode contentNode, String chatId) throws TelegramApiException;

    @Override
    public boolean supports(ContentNode contentNode) {
        if (contentNode.getType() != ContentType.MEDIA) return false;
        return contentNode.getMedia() != null && contentNode.getMedia().getType().equalsIgnoreCase(getMediaType());
    }

    @Override
    public Message handle(ContentNode contentNode, String chatId) {
        try {
            return execute(contentNode, chatId);
        } catch (TelegramApiException e) {
            log.error("Failed to handle media in chat: {}", chatId, e);
            return null;
        }
    }

}
