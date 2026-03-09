package com.github.jawisimo.botengine.interaction.navigator;

import com.github.jawisimo.botengine.interaction.content.ContentExecutor;
import com.github.jawisimo.botengine.interaction.keyboard.KeyboardExecutor;
import com.github.jawisimo.botengine.model.DialogNode;
import com.github.jawisimo.botengine.service.MessageCleanupService;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Executes dialog nodes by rendering content and keyboard interactions.
 *
 * <p>Coordinates node execution flow and ensures sequential processing per chat. Delegates content
 * rendering to {@link ContentExecutor} and keyboard creation to {@link KeyboardExecutor}.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class NodeExecutor {

  private final ContentExecutor contentExecutor;
  private final KeyboardExecutor keyboardExecutor;
  private final MessageCleanupService cleanupService;
  private final ConcurrentHashMap<String, Object> chatLocks = new ConcurrentHashMap<>();

  /**
   * Executes the given dialog node for the specified chat.
   *
   * <p>Ensures that node execution is synchronized per chat to prevent concurrent message rendering
   * conflicts.
   *
   * @param node the dialog node to execute
   * @param chatId the chat identifier
   */
  public void execute(DialogNode node, String chatId) {
    Object lock = chatLocks.computeIfAbsent(chatId, k -> new Object());

    synchronized (lock) {
      cleanupService.clearLastNode(chatId);
      contentExecutor.execute(node, chatId);
      keyboardExecutor.execute(node, chatId);
    }
  }
}
