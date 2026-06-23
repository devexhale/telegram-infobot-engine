package io.github.devexhale.botengine.execution.broadcast;

import io.github.devexhale.botengine.annotation.ConditionalOnBroadcastEnabled;
import io.github.devexhale.botengine.domain.broadcast.BroadcastNode;
import io.github.devexhale.botengine.service.SubscriberService;
import io.github.devexhale.botengine.storage.definition.DefinitionStorage;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

  public void execute(String nodeId, Set<String> subscribers) {
    BroadcastNode node = broadcastStorage.getNode(nodeId);

    for (String chatId : subscribers) {
      try {
        broadcastNodeExecutor.execute(node, chatId);
        log.info("Broadcast node '{}' sent to chat. ChatID={}", nodeId, chatId);
      } catch (Exception e) {
        if (isForbiddenError(e)) {
          subscriberService.unsubscribe(chatId);
          log.warn(
              "User unsubscribed during broadcast because of 403 Forbidden. ChatID={}", chatId);
          continue;
        }

        log.error("Failed to send broadcast node '{}'. ChatID={}", nodeId, chatId, e);
      }
    }
  }

  private boolean isForbiddenError(Exception e) {
    String msg = e.getMessage();
    return msg != null
        && (msg.contains(FORBIDDEN_RESPONSE_CODE) || msg.contains(FORBIDDEN_RESPONSE_MSG));
  }
}
