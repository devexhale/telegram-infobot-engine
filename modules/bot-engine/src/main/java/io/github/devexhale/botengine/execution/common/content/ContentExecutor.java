package io.github.devexhale.botengine.execution.common.content;

import io.github.devexhale.botengine.domain.content.ContentNode;
import io.github.devexhale.botengine.execution.common.content.handler.ContentHandlerRegistry;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;

/**
 * Orchestrates the execution of a list of content nodes for a specific chat.
 *
 * <p>Delegates the processing of each {@link ContentNode} to its corresponding handler via the
 * {@link ContentHandlerRegistry}.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class ContentExecutor {

  private final ContentHandlerRegistry registry;

  /**
   * Processes and sends a list of content nodes to the specified chat.
   *
   * @param content the list of content nodes to process
   * @param chatId the target chat identifier
   * @return a list of sent Telegram {@link Message} objects
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
