package io.github.devexhale.botengine.execution.common.content.handler;

import io.github.devexhale.botengine.domain.content.ContentNode;
import io.github.devexhale.botengine.domain.content.ContentType;
import org.telegram.telegrambots.meta.api.objects.message.Message;

/**
 * Strategy interface for handling dialog content execution.
 *
 * <p>Implementations process specific {@link ContentNode} types. Each handler produces a Telegram
 * {@link Message}.
 *
 * @since 1.0
 */
public interface ContentHandler {

  ContentType type();

  /**
   * Processes the content node and sends the resulting message.
   *
   * @param contentNode the content node to handle
   * @param chatId the chat identifier
   * @return the sent Telegram message
   */
  Message handle(ContentNode contentNode, String chatId);
}
