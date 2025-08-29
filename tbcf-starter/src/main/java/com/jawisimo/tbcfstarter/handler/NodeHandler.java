package com.jawisimo.tbcfstarter.handler;

import com.jawisimo.tbcfstarter.exception.MediaLoadingException;
import com.jawisimo.tbcfstarter.model.ContentNode;
import com.jawisimo.tbcfstarter.model.DialogNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NodeHandler {

    private final List<ContentHandler> contentHandlers;
    private final KeyboardHandler keyboardHandler;

    /**
     * Обробка ноди: спершу контент, потім кнопки.
     */
    public void handle(DialogNode node, String chatId) {
        processContent(node.content(), chatId);
        processKeyboard(node, chatId);
    }

    /**
     * Обробляє всі ContentNode.
     */
    private void processContent(List<ContentNode> contents, String chatId) {
        if (contents == null || contents.isEmpty()) {
            return;
        }

        for (ContentNode contentNode : contents) {
            validateMedia(contentNode);
            dispatchToHandler(contentNode, chatId);
        }
    }

    /**
     * Валідує медіа і кидає виняток, якщо тип не заданий або не підтримується.
     */
    private void validateMedia(ContentNode contentNode) {
        if (contentNode.getMedia() == null) {
            return;
        }

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

    /**
     * Делегує обробку контенту конкретному ContentHandler.
     */
    private void dispatchToHandler(ContentNode contentNode, String chatId) {
        contentHandlers.stream()
                .filter(h -> h.supports(contentNode))
                .forEach(h -> h.handle(contentNode, chatId));
    }

    /**
     * Відправка клавіатури користувачу.
     */
    private void processKeyboard(DialogNode node, String chatId) {
        if (node.buttons() != null && !node.buttons().isEmpty()) {
            keyboardHandler.handle(node, chatId);
        }
    }
}
