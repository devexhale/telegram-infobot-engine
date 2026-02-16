package com.jawisimo.tbcfstarter.interaction.node;

import com.jawisimo.tbcfstarter.interaction.media.MediaExecutor;
import com.jawisimo.tbcfstarter.interaction.keyboard.KeyboardExecutor;
import com.jawisimo.tbcfstarter.interaction.node.model.DialogNode;
import com.jawisimo.tbcfstarter.service.MessageCleanupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class NodeExecutor {
  private final MediaExecutor mediaExecutor;
  private final KeyboardExecutor keyboardExecutor;
  private final MessageCleanupService cleanupService;
  private final ConcurrentHashMap<String, Object> chatLocks = new ConcurrentHashMap<>();

  public void execute(DialogNode node, String chatId) {
    Object lock = chatLocks.computeIfAbsent(chatId, k -> new Object());

    synchronized (lock) {
      cleanupService.clearLastNode(chatId);
      mediaExecutor.execute(node, chatId);
      keyboardExecutor.execute(node, chatId);
    }
  }
}
