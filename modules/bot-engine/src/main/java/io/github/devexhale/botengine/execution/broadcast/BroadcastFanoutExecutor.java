package io.github.devexhale.botengine.execution.broadcast;

import io.github.devexhale.botengine.annotation.ConditionalOnBroadcastEnabled;
import io.github.devexhale.botengine.domain.broadcast.BroadcastNode;
import io.github.devexhale.botengine.service.SubscriberService;
import io.github.devexhale.botengine.storage.definition.DefinitionStorage;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Executes a broadcast node for a set of subscribers.
 *
 * <p>Iterates through the provided subscribers and sends the broadcast node. Automatically
 * unsubscribes users if a 403 Forbidden error occurs.
 *
 * @since 1.0
 */
@Component
@ConditionalOnBroadcastEnabled
@RequiredArgsConstructor
@Slf4j
public class BroadcastFanoutExecutor {

  private static final String FORBIDDEN_RESPONSE_CODE = "403";
  private static final String FORBIDDEN_RESPONSE_MSG = "Forbidden";

  private final DefinitionStorage<BroadcastNode> broadcastStorage;
  private final BroadcastNodeExecutor broadcastNodeExecutor;
  private final SubscriberService subscriberService;

  /**
   * Sends the specified broadcast node to all provided subscribers.
   *
   * @param nodeId the ID of the broadcast node to send
   * @param subscribers the set of chat IDs to send the broadcast to
   */
  public void execute(String nodeId, Set<String> subscribers) {
    BroadcastNode node = broadcastStorage.getNode(nodeId);

    for (String chatId : subscribers) {
      if (Thread.currentThread().isInterrupted()) {
        log.warn("Broadcast for node '{}' was interrupted by shutdown. Stopping fanout.", nodeId);
        break;
      }

      processSubscriber(nodeId, node, chatId);
    }
  }

  private void processSubscriber(String nodeId, BroadcastNode node, String chatId) {
    try {
      broadcastNodeExecutor.execute(node, chatId);
      log.info("Broadcast node '{}' sent to chat. ChatID={}", nodeId, chatId);
    } catch (Exception e) {
      if (isForbiddenError(e)) {
        subscriberService.unsubscribe(chatId);
        log.warn("User unsubscribed during broadcast because of 403 Forbidden. ChatID={}", chatId);
        return;
      }

      log.error("Failed to send broadcast node '{}'. ChatID={}", nodeId, chatId, e);
    }
  }

  private boolean isForbiddenError(Exception e) {
    String msg = e.getMessage();
    return msg != null
        && (msg.contains(FORBIDDEN_RESPONSE_CODE) || msg.contains(FORBIDDEN_RESPONSE_MSG));
  }
}
