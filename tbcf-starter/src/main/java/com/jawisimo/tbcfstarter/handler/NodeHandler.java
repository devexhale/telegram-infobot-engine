package com.jawisimo.tbcfstarter.handler;

import com.jawisimo.tbcfstarter.model.ContentNode;
import com.jawisimo.tbcfstarter.model.ContentType;
import com.jawisimo.tbcfstarter.model.DialogNode;
import com.jawisimo.tbcfstarter.service.MessageCleanupService;
import com.jawisimo.tbcfstarter.validator.ContentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class NodeHandler {

    private final List<ContentHandler> contentHandlers;
    private final KeyboardMarkupHandler keyboardHandler;
    private final MessageCleanupService cleanupService;
    private final ContentValidator contentValidator;
    private final ConcurrentHashMap<String, Object> chatLocks = new ConcurrentHashMap<>();
    private final TelegramClient client;

    public void handle(DialogNode node, String chatId) {
        Object lock = chatLocks.computeIfAbsent(chatId, k -> new Object());
        synchronized (lock) {
            cleanupService.clearLastNode(chatId);
            processContent(node, chatId);
            processKeyboard(node, chatId);
        }
    }

    private void processContent(DialogNode node, String chatId) {
        if (node.content() == null) return;

        for (ContentNode contentNode : node.content()) {
            contentValidator.validate(contentNode);
            handleContentNode(contentNode, chatId);
        }
    }

    private void handleContentNode(ContentNode contentNode, String chatId) {
        for (ContentHandler handler : contentHandlers) {
            if (!handler.supports(contentNode)) continue;

            Message tempMsg = null;
            if (isSlowMedia(contentNode)) {
                tempMsg = sendTempMessage(chatId);
            }

            Message sent = handler.handle(contentNode, chatId);
            if (sent != null) {
                cleanupService.registerMessage(chatId, sent.getMessageId());
            }

            if (tempMsg != null) {
                cleanupService.deleteMessage(chatId, tempMsg.getMessageId());
            }
        }
    }

    private void processKeyboard(DialogNode node, String chatId) {
        Message keyboardMsg = keyboardHandler.handle(node, chatId);
        if (keyboardMsg != null) {
            cleanupService.registerMessage(chatId, keyboardMsg.getMessageId());
        }
    }

    private boolean isSlowMedia(ContentNode node) {
        return node.getType() == ContentType.MEDIA && node.getMedia() != null;
    }

    private Message sendTempMessage(String chatId) {
        try {
            Message msg = client.execute(
                    SendMessage.builder()
                            .chatId(chatId)
                            .text("Uploading media. Wait...")
                            .build()
            );
            cleanupService.registerMessage(chatId, msg.getMessageId());
            return msg;
        } catch (TelegramApiException e) {
            log.error("Failed to send temp message: {}", e.getMessage(), e);
            return null;
        }
    }
}
