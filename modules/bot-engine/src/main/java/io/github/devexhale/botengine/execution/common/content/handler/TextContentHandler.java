package io.github.devexhale.botengine.execution.common.content.handler;

import io.github.devexhale.botengine.diagnostics.exception.TelegramMessageSendException;
import io.github.devexhale.botengine.domain.content.ContentNode;
import io.github.devexhale.botengine.domain.content.ContentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * {@link ContentHandler} implementation for plain text content.
 *
 * <p>Sends a simple text message when the content type is {@link ContentType#TEXT}.
 *
 * @since 1.0
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class TextContentHandler implements ContentHandler {

  private final TelegramClient client;

  @Override
  public ContentType type() {
    return ContentType.TEXT;
  }

  @Override
  public Message handle(ContentNode node, String chatId) {
    SendMessage request = new SendMessage(chatId, node.text());

    try {
      return client.execute(request);
    } catch (TelegramApiException e) {
      log.error("Failed to send text content in chat. ChatID={}", chatId);
      throw new TelegramMessageSendException(e);
    }
  }
}
