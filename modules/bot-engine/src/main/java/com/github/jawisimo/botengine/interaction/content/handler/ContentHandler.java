package com.github.jawisimo.botengine.interaction.content.handler;

import com.github.jawisimo.botengine.interaction.node.model.ContentNode;
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

  /**
   * Determines whether this handler supports the given content node.
   *
   * @param contentNode the content node to evaluate
   * @return {@code true} if the handler can process the content
   */
  boolean canHandle(ContentNode contentNode);

  /**
   * Processes the content node and sends the resulting message.
   *
   * @param contentNode the content node to handle
   * @param chatId the chat identifier
   * @return the sent Telegram message
   */
  Message handle(ContentNode contentNode, String chatId);
}
