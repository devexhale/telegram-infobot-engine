package com.github.jawisimo.botengine.interaction.content.handler;

import com.github.jawisimo.botengine.interaction.node.model.ContentNode;
import com.github.jawisimo.botengine.interaction.node.model.Media;
import com.github.jawisimo.botengine.loader.MediaFileLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * {@link ContentHandler} implementation for audio media content.
 *
 * <p>Sends an audio file defined by {@link Media} when the media type is {@code AUDIO}.
 *
 * @since 1.0
 */
@Component
@Slf4j
public class AudioMediaHandler extends AbstractMediaHandler {

  AudioMediaHandler(TelegramClient client, MediaFileLoader mediaFileLoader) {
    super(client, mediaFileLoader);
  }

  /**
   * Sends an audio message to the specified chat.
   *
   * @param contentNode the content node containing media configuration
   * @param chatId the chat identifier
   * @return the sent Telegram message, or {@code null} if sending failed
   */
  @Override
  public Message handle(ContentNode contentNode, String chatId) {
    Media media = getMedia(contentNode);
    SendAudio request =
        SendAudio.builder()
            .chatId(chatId)
            .audio(getMediaFile(media.fileName()))
            .caption(media.caption())
            .build();

    try {
      return getClient().execute(request);
    } catch (TelegramApiException e) {
      log.error("Failed to handle audio in chat: {}", chatId, e);
      return null;
    }
  }

  /**
   * Returns the media type supported by this handler.
   *
   * @return {@code AUDIO}
   */
  @Override
  String getMediaType() {
    return "AUDIO";
  }
}
