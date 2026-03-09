package com.github.jawisimo.botengine.interaction.content.handler;

import com.github.jawisimo.botengine.model.ContentNode;
import com.github.jawisimo.botengine.model.ContentType;
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

  /**
   * Returns {@code true} if the content node represents text.
   *
   * @param contentNode the content node to evaluate
   * @return {@code true} if this handler supports the node
   */
  @Override
  public boolean canHandle(ContentNode contentNode) {
    return contentNode.type() == ContentType.TEXT;
  }

  /**
   * Sends the text message to the specified chat.
   *
   * @param contentNode the text content node
   * @param chatId the chat identifier
   * @return the sent Telegram message, or {@code null} if sending failed
   */
  @Override
  public Message handle(ContentNode contentNode, String chatId) {
    String text = contentNode.text();
    SendMessage sendMessage = new SendMessage(chatId, text);

    try {
      return client.execute(sendMessage);
    } catch (TelegramApiException e) {
      log.error("Failed to handle text in chat: {}", chatId, e);
      return null;
    }
  }
}
