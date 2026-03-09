package com.github.jawisimo.botengine.interaction.content.handler;

import com.github.jawisimo.botengine.model.ContentNode;
import com.github.jawisimo.botengine.model.Media;
import com.github.jawisimo.botengine.loader.MediaFileLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * {@link ContentHandler} implementation for document media content.
 *
 * <p>Sends a document file defined by {@link Media} when the media type is {@code DOCUMENT}.
 *
 * @since 1.0
 */
@Component
@Slf4j
public class DocumentMediaHandler extends AbstractMediaHandler {

  DocumentMediaHandler(TelegramClient client, MediaFileLoader mediaFileLoader) {
    super(client, mediaFileLoader);
  }

  /**
   * Sends a document message to the specified chat.
   *
   * @param contentNode the content node containing media configuration
   * @param chatId the chat identifier
   * @return the sent Telegram message, or {@code null} if sending failed
   */
  @Override
  public Message handle(ContentNode contentNode, String chatId) {
    Media media = getMedia(contentNode);
    SendDocument request =
        SendDocument.builder()
            .chatId(chatId)
            .document(getMediaFile(media.fileName()))
            .caption(media.caption())
            .build();

    try {
      return getClient().execute(request);
    } catch (TelegramApiException e) {
      log.error("Failed to handle document in chat: {}", chatId, e);
      return null;
    }
  }

  /**
   * Returns the media type supported by this handler.
   *
   * @return {@code DOCUMENT}
   */
  @Override
  String getMediaType() {
    return "DOCUMENT";
  }
}
