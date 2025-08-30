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


    public void handle(DialogNode node, String chatId) {
        List<ContentNode> content = node.content();

        content.forEach(contentNode -> {
            if (contentNode.getMedia() != null) {
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
            contentHandlers.stream()
                    .filter(contentHandler -> contentHandler.supports(contentNode))
                    .forEach(contentHandler -> contentHandler.handle(contentNode, chatId));
        });

        keyboardHandler.handle(node, chatId);

    }
}
