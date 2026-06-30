package io.github.devexhale.botengine.execution.broadcast;

import io.github.devexhale.botengine.annotation.ConditionalOnBroadcastEnabled;
import io.github.devexhale.botengine.domain.broadcast.BroadcastNode;
import io.github.devexhale.botengine.execution.common.content.ContentExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Orchestrates the execution of a single broadcast node for a specific chat.
 *
 * <p>Delegates the sending of content and inline keyboard to their respective executors.
 *
 * @since 1.0
 */
@Component
@ConditionalOnBroadcastEnabled
@RequiredArgsConstructor
@Slf4j
public class BroadcastNodeExecutor {

  private final ContentExecutor contentExecutor;
  private final BroadcastKeyboardExecutor keyboardExecutor;

  /**
   * Executes the content and inline keyboard for the given broadcast node.
   *
   * @param node the broadcast node to execute
   * @param chatId the target chat ID
   */
  public void execute(BroadcastNode node, String chatId) {
    contentExecutor.execute(node.content(), chatId);
    keyboardExecutor.execute(node, chatId);
  }
}
