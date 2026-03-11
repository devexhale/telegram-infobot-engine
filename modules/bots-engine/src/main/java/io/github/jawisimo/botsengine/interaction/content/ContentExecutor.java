package io.github.jawisimo.botsengine.interaction.content;

import io.github.jawisimo.botsengine.interaction.content.handler.ContentHandler;
import io.github.jawisimo.botsengine.model.ContentNode;
import io.github.jawisimo.botsengine.model.DialogNode;
import io.github.jawisimo.botsengine.repository.MessageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;

/**
 * Executes dialog node content using registered {@link ContentHandler} implementations.
 *
 * <p>Validates each content node, delegates processing to a matching handler, and stores sent
 * message identifiers for later cleanup.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class ContentExecutor {

  private final List<ContentHandler> contentHandlers;
  private final MessageRepository messageRepository;

  /**
   * Executes all content blocks of the given dialog node for the specified chat.
   *
   * @param node the dialog node containing content definitions
   * @param chatId the chat identifier
   */
  public void execute(DialogNode node, String chatId) {
    if (node.content() == null) {
      return;
    }

    for (ContentNode contentNode : node.content()) {
      for (ContentHandler handler : contentHandlers) {
        if (handler.canHandle(contentNode)) {
          Message sent = handler.handle(contentNode, chatId);
          if (sent != null) {
            messageRepository.save(chatId, sent.getMessageId());
          }
        }
      }
    }
  }
}
