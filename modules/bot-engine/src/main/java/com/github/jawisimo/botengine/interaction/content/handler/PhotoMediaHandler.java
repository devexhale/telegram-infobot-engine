package com.github.jawisimo.botengine.interaction.content.handler;

import com.github.jawisimo.botengine.interaction.node.model.Media;
import com.github.jawisimo.botengine.loader.MediaFileLoader;
import com.github.jawisimo.botengine.interaction.node.model.ContentNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@Slf4j
public class PhotoMediaHandler extends AbstractMediaHandler {

  PhotoMediaHandler(TelegramClient client, MediaFileLoader mediaFileLoader) {
    super(client, mediaFileLoader);
  }

  @Override
  public Message handle(ContentNode contentNode, String chatId) {
    Media media = getMedia(contentNode);
    SendPhoto request =
        SendPhoto.builder()
            .chatId(chatId)
            .photo(getMediaFile(media.fileName()))
            .caption(media.caption())
            .build();

    try {
      return getClient().execute(request);
    } catch (TelegramApiException e) {
      log.error("Failed to photo in chat: {}", chatId, e);
      return null;
    }
  }

  @Override
  String getMediaType() {
    return "PHOTO";
  }
}
