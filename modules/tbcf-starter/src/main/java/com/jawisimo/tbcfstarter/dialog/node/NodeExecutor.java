package com.jawisimo.tbcfstarter.dialog.node;

import com.jawisimo.tbcfstarter.dialog.media.MediaExecutor;
import com.jawisimo.tbcfstarter.dialog.keyboard.KeyboardExecutor;
import com.jawisimo.tbcfstarter.dialog.node.model.DialogNode;
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
