package io.github.devexhale.botengine.execution.dialog.navigator;

import io.github.devexhale.botengine.domain.dialog.DialogNode;
import io.github.devexhale.botengine.execution.common.content.ContentExecutor;
import io.github.devexhale.botengine.execution.dialog.keyboard.DialogKeyboardExecutor;
import io.github.devexhale.botengine.execution.support.ChatLockRegistry;
import io.github.devexhale.botengine.execution.support.MessageCleanupManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;

/**
 * Executes dialog nodes for a specific chat by rendering content and keyboard.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class DialogNodeExecutor {

  private final ContentExecutor contentExecutor;
  private final DialogKeyboardExecutor dialogKeyboardExecutor;
  private final MessageCleanupManager messageCleanupManager;
  private final ChatLockRegistry chatLockRegistry;

  /**
   * Executes the given dialog node for the specified chat.
   *
   * @param node the dialog node to execute
   * @param chatId the chat identifier
   */
  public void execute(DialogNode node, String chatId) {
    Lock lock = chatLockRegistry.getLock(chatId);
    lock.lock();
    try {
      messageCleanupManager.cleanLastNode(chatId);
      renderNode(node, chatId);
    } finally {
      lock.unlock();
    }
  }

  /**
   * Renders content and keyboard for the given node.
   *
   * @param node the dialog node to render
   * @param chatId the chat identifier
   */
  private void renderNode(DialogNode node, String chatId) {
    List<Message> sentMessages = new ArrayList<>();

    try {
      sentMessages.addAll(contentExecutor.execute(node.content(), chatId));
      sentMessages.addAll(dialogKeyboardExecutor.execute(node, chatId));
    } finally {
      if (!sentMessages.isEmpty()) {
        messageCleanupManager.registerMessagesForCleanup(chatId, sentMessages);
      }
    }
  }
}
