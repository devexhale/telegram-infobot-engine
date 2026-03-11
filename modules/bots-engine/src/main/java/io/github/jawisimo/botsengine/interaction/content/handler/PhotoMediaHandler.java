package io.github.jawisimo.botsengine.interaction.content.handler;

import io.github.jawisimo.botsengine.model.ContentNode;
import io.github.jawisimo.botsengine.model.Media;
import io.github.jawisimo.botsengine.loader.MediaFileLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * {@link ContentHandler} implementation for photo media content.
 *
 * <p>Sends a photo defined by {@link Media} when the media type is {@code PHOTO}.
 *
 * @since 1.0
 */
@Component
@Slf4j
public class PhotoMediaHandler extends AbstractMediaHandler {

  PhotoMediaHandler(TelegramClient client, MediaFileLoader mediaFileLoader) {
    super(client, mediaFileLoader);
  }

  /**
   * Sends a photo message to the specified chat.
   *
   * @param contentNode the content node containing media configuration
   * @param chatId the chat identifier
   * @return the sent Telegram message, or {@code null} if sending failed
   */
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

  /**
   * Returns the media type supported by this handler.
   *
   * @return {@code PHOTO}
   */
  @Override
  String getMediaType() {
    return "PHOTO";
  }
}
