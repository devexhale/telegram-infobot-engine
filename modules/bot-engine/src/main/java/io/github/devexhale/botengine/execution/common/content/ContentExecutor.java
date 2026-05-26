package io.github.devexhale.botengine.execution.common.content;

import io.github.devexhale.botengine.domain.content.ContentNode;
import io.github.devexhale.botengine.execution.common.content.handler.ContentHandler;
import io.github.devexhale.botengine.execution.common.content.handler.ContentHandlerRegistry;
import java.util.ArrayList;
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

  private final ContentHandlerRegistry registry;

  /**
   * Executes all content blocks of the given dialog node for the specified chat.
   *
   * @param content list
   * @param chatId the chat identifier
   */
  public List<Message> execute(List<ContentNode> content, String chatId) {
    if (content == null || content.isEmpty()) {
      return List.of();
    }

    List<Message> sentMessages = new ArrayList<>();

    for (ContentNode node : content) {
      Message message = registry.get(node.type()).orElseThrow().handle(node, chatId);
      sentMessages.add(message);
    }

    return sentMessages;
  }
}
