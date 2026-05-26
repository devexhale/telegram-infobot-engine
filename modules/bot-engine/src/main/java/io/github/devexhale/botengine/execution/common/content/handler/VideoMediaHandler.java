package io.github.devexhale.botengine.execution.common.content.handler;

import io.github.devexhale.botengine.diagnostics.exception.TelegramMessageSendException;
import io.github.devexhale.botengine.domain.content.ContentNode;
import io.github.devexhale.botengine.domain.content.ContentType;
import io.github.devexhale.botengine.domain.content.Media;
import io.github.devexhale.botengine.loader.MediaFileLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * {@link ContentHandler} implementation for video media content.
 *
 * <p>Sends a video file defined by {@link Media} when the media type is {@code VIDEO}.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class VideoMediaHandler implements ContentHandler {

  private final TelegramClient client;
  private final MediaFileLoader mediaFileLoader;

  @Override
  public ContentType type() {
    return ContentType.VIDEO;
  }

  /**
   * Sends a video message to the specified chat.
   *
   * @param node the content node containing media configuration
   * @param chatId the chat identifier
   * @return an Optional containing the sent message, or empty if sending failed
   */
  @Override
  public Message handle(ContentNode node, String chatId) {
    SendVideo request = createRequest(node, chatId);

    try {
      return client.execute(request);
    } catch (TelegramApiException e) {
      log.error("Failed to send video in chat. ChatID={}", chatId);
      throw new TelegramMessageSendException(e);
    }
  }

  private SendVideo createRequest(ContentNode node, String chatId) {
    return SendVideo.builder()
        .chatId(chatId)
        .video(mediaFileLoader.load(node.fileName()))
        .caption(node.caption())
        .build();
  }
}
