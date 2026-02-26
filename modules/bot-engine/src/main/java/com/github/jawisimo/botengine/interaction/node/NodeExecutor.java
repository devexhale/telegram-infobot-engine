package com.github.jawisimo.botengine.interaction.node;

import com.github.jawisimo.botengine.interaction.content.ContentExecutor;
import com.github.jawisimo.botengine.interaction.keyboard.KeyboardExecutor;
import com.github.jawisimo.botengine.interaction.node.model.DialogNode;
import com.github.jawisimo.botengine.service.MessageCleanupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class NodeExecutor {

  private final ContentExecutor contentExecutor;
  private final KeyboardExecutor keyboardExecutor;
  private final MessageCleanupService cleanupService;
  private final ConcurrentHashMap<String, Object> chatLocks = new ConcurrentHashMap<>();

  public void execute(DialogNode node, String chatId) {
    Object lock = chatLocks.computeIfAbsent(chatId, k -> new Object());

    synchronized (lock) {
      cleanupService.clearLastNode(chatId);
      contentExecutor.execute(node, chatId);
      keyboardExecutor.execute(node, chatId);
    }
  }
}
