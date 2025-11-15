package com.jawisimo.tbcfstarter.dialog;

import com.jawisimo.tbcfstarter.content.ContentExecutor;
import com.jawisimo.tbcfstarter.keyboard.KeyboardExecutor;
import com.jawisimo.tbcfstarter.model.DialogNode;
import com.jawisimo.tbcfstarter.service.MessageCleanupService;
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
