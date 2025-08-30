package com.jawisimo.tbcfstarter.handler;

import com.jawisimo.tbcfstarter.exception.MediaLoadingException;
import com.jawisimo.tbcfstarter.model.ContentNode;
import com.jawisimo.tbcfstarter.model.DialogNode;
import com.jawisimo.tbcfstarter.service.MessageCleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NodeHandler {
    private final List<ContentHandler> contentHandlers;
    private final KeyboardMarkupHandler keyboardHandler;
    private final MessageCleanupService cleanupService;

    public void handle(DialogNode node, String chatId) {
        cleanupService.clearLastNode(chatId);
        contentProcess(node, chatId);
        keyboardProcess(node, chatId);
    }

    private void contentProcess(DialogNode node, String chatId) {
        List<ContentNode> content = node.content();
        if (content != null) {
            for (ContentNode contentNode : content) {
                validateMedia(contentNode);

                contentHandlers.stream()
                        .filter(contentHandler -> contentHandler.supports(contentNode))
                        .forEach(contentHandler -> {
                            try {
                                Message sent = contentHandler.handle(contentNode, chatId);
                                if (sent != null) {
                                    cleanupService.registerMessage(chatId, sent.getMessageId());
                                }
                            } catch (Exception e) {
                                log.error("Failed to send content: {}", e.getMessage(), e);
                            }
                        });
            }
        }
    }

    private void keyboardProcess(DialogNode node, String chatId) {
        try {
            Message keyboardMsg = keyboardHandler.handle(node, chatId);
            if (keyboardMsg != null) {
                cleanupService.registerMessage(chatId, keyboardMsg.getMessageId());
            }
        } catch (Exception e) {
            log.error("Failed to send keyboard: {}", e.getMessage(), e);
        }
    }

    private void validateMedia(ContentNode contentNode) {
        if (contentNode.getMedia() == null) return;

        String type = contentNode.getMedia().getType();
        String fileName = contentNode.getMedia().getFileName();

        if (type == null || type.isBlank()) {
            throw new MediaLoadingException("Media type is missing for file: " + fileName);
        }

        boolean supported = contentHandlers.stream()
                .anyMatch(h -> h.supports(contentNode));

        if (!supported) {
            throw new MediaLoadingException(
                    "No handler found for media type: " + type + " (file: " + fileName + ")"
            );
        }
    }
}
