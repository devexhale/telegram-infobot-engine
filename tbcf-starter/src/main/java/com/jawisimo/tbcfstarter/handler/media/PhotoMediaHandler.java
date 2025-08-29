package com.jawisimo.tbcfstarter.handler.media;

import com.jawisimo.tbcfstarter.handler.ContentHandler;
import com.jawisimo.tbcfstarter.model.ContentNode;
import com.jawisimo.tbcfstarter.model.ContentType;
import com.jawisimo.tbcfstarter.support.InputMediaFileResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaBotMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@Slf4j
@RequiredArgsConstructor
public class PhotoMediaHandler implements ContentHandler {
    private final TelegramClient client;

    private final InputMediaFileResolver mediaFileResolver;

    private static final String MEDIA_TYPE = "PHOTO";

    @Override
    public boolean supports(ContentNode contentNode) {
        if (contentNode.getType() != ContentType.MEDIA) {
            return false;
        }
        return contentNode.getMedia() != null && contentNode.getMedia().getType().equalsIgnoreCase(MEDIA_TYPE);
    }


    @Override
    public void handle(ContentNode contentNode, String chatId) {
        InputFile inputFile = mediaFileResolver.getMediaFile(contentNode.getMedia());
        executePhotoMessage(SendPhoto.builder()
                .chatId(chatId)
                .photo(inputFile)
                .caption(contentNode.getMedia().getCaption())
                .build());
    }

    private void executePhotoMessage(SendMediaBotMethod<Message> message)  {
        try {
            client.execute((SendPhoto) message);
        } catch (TelegramApiException e) {
            log.error("Telegram API Exception: {}", e.getMessage(), e);
        }
    }
}
