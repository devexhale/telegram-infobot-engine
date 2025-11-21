package com.jawisimo.tbcfstarter.validator;

import com.jawisimo.tbcfstarter.interaction.command.commandset.StartCommand;
import com.jawisimo.tbcfstarter.exception.DialogLoadingException;
import com.jawisimo.tbcfstarter.interaction.media.handler.MediaHandler;
import com.jawisimo.tbcfstarter.interaction.node.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DialogValidator {

    public void validateStartNode(DialogMap dialogMap, String fileName) {
        if (!dialogMap.containsNodeKey(StartCommand.COMMAND_NAME)) {
            throw new DialogLoadingException("Dialog must contain '/start' node in file: " + fileName);
        }
    }

    public void validateContentNode(ContentNode contentNode, List<MediaHandler> mediaHandlers) {
        validateContentType(contentNode);

        if (contentNode.getMedia() != null) {
            validateMedia(contentNode.getMedia(), contentNode, mediaHandlers);
        }
    }

    public void validateMediaFileName(String mediaFileName) {
        if (mediaFileName == null || mediaFileName.isBlank()) {
            throw new DialogLoadingException("Media file_name is missing or blank");
        }
    }

    public void validateButtons(DialogNode node) {
        if ((node.buttons() == null || node.buttons().isEmpty())) {
            throw new DialogLoadingException("Buttons are missing");
        }

        ButtonType type = node.buttonType();

        for (Button button : node.buttons()) {
            validateButton(button, type);
        }
    }

    // Приватний метод для перевірки обов'язкового типу
    private void validateContentType(ContentNode contentNode) {
        if (contentNode.getType() == null) {
            throw new DialogLoadingException("Content type is missing or not valid");
        }
    }

    // Приватний метод для перевірки медіа
    private void validateMedia(Media media, ContentNode contentNode, List<MediaHandler> mediaHandlers) {
        validateMediaType(media);
        validateMediaSupported(media, contentNode, mediaHandlers);
    }

    // Перевірка, що тип медіа заданий
    private void validateMediaType(Media media) {
        String type = media.getType();
        String fileName = media.getFileName();

        if (type == null || type.isBlank()) {
            throw new DialogLoadingException("Media type is missing for file: " + fileName);
        }
    }


    private void validateMediaSupported(Media media, ContentNode contentNode, List<MediaHandler> mediaHandlers) {
        String type = media.getType();
        String fileName = media.getFileName();
        boolean supported = mediaHandlers.stream().anyMatch(h -> h.canHandle(contentNode));

        if (!supported) {
            throw new DialogLoadingException(
                    "No handler found for media type: " + type + " (file: " + fileName + ")"
            );
        }
    }

    private void validateButton(Button button, ButtonType type) {
        String label = button.getLabel();

        if (label == null || label.isBlank()) {
            throw new DialogLoadingException("Button label is missing or blank");
        }

        String url = button.getUrl();
        String next = button.getNext();
        boolean hasUrl = url != null && !url.isBlank();
        boolean hasNext = next != null && !next.isBlank();

        if (type == ButtonType.REPLY) {
            validateReplyButton(hasUrl, hasNext);
        } else {
            validateInlineButton(hasUrl, hasNext);
        }
    }

    private void validateReplyButton(boolean hasUrl, boolean hasNext) {
        if (hasUrl) {
            throw new DialogLoadingException("Reply button cannot have a URL");
        }

        if (!hasNext) {
            throw new DialogLoadingException("Reply button must have a next (callback text)");
        }
    }

    private void validateInlineButton(boolean hasUrl, boolean hasNext) {
        if (!hasUrl && !hasNext) {
            throw new DialogLoadingException("Inline button must have either URL or next");
        }

        if (hasUrl && hasNext) {
            throw new DialogLoadingException("Inline button cannot have both URL and next");
        }
    }
}
