package io.github.devexhale.botengine.execution.common.content.handler;

import io.github.devexhale.botengine.diagnostics.exception.TelegramMessageSendException;
import io.github.devexhale.botengine.domain.content.ContentNode;
import io.github.devexhale.botengine.domain.content.ContentType;
import io.github.devexhale.botengine.domain.content.Media;
import io.github.devexhale.botengine.loader.MediaFileLoader;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Slf4j
public class AudioMediaHandler implements ContentHandler {

  private final TelegramClient client;
  private final MediaFileLoader mediaFileLoader;

  @Override
  public ContentType type() {
    return ContentType.AUDIO;
  }

  /**
   * Sends an audio message to the specified chat.
   *
   * @param node the content node containing media configuration
   * @param chatId the chat identifier
   * @return an Optional containing the sent message, or empty if sending failed
   */
  @Override
  public Message handle(ContentNode node, String chatId) {
    SendAudio request = createRequest(node, chatId);

    try {
      return client.execute(request);
    } catch (TelegramApiException e) {
      log.error("Failed to send audio in chat. ChatID={}", chatId);
      throw new TelegramMessageSendException(e);
    }
  }

  private SendAudio createRequest(ContentNode node, String chatId) {
    return SendAudio.builder()
        .chatId(chatId)
        .audio(mediaFileLoader.load(node.fileName()))
        .caption(node.caption())
        .build();
  }
}
