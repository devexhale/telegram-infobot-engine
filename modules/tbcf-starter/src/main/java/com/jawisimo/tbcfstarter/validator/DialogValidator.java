package com.jawisimo.tbcfstarter.validator;

import com.jawisimo.tbcfstarter.command.StartCommand;
import com.jawisimo.tbcfstarter.exception.DialogLoadingException;
import com.jawisimo.tbcfstarter.handler.ContentHandler;
import com.jawisimo.tbcfstarter.model.Button;
import com.jawisimo.tbcfstarter.model.ButtonType;
import com.jawisimo.tbcfstarter.model.ContentNode;
import com.jawisimo.tbcfstarter.model.DialogNode;
import com.jawisimo.tbcfstarter.parser.DialogParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DialogValidator {

    public void validateContentNode(ContentNode contentNode, List<ContentHandler> contentHandlers) {
        if (contentNode.getMedia() == null) return;

        String type = contentNode.getMedia().getType();
        String fileName = contentNode.getMedia().getFileName();

        if (type == null || type.isBlank()) {
            throw new DialogLoadingException("Media type is missing for file: " + fileName);
        }

        boolean supported = contentHandlers.stream()
                .anyMatch(h -> h.supports(contentNode));

        if (!supported) {
            throw new DialogLoadingException(
                    "No handler found for media type: " + type + " (file: " + fileName + ")"
            );
        }
    }

    public void validateDialogFile(InputStream is, String fileName) {
        if (is == null) {
            throw new DialogLoadingException("Dialog file not found: " + fileName);
        }
    }

    public void validateParsers(List<DialogParser> matchingParsers, String fileName) {
        if (matchingParsers.isEmpty()) {
            throw new DialogLoadingException("No suitable parser found for file: " + fileName);
        }
        if (matchingParsers.size() > 1) {
            throw new DialogLoadingException(
                    "Multiple parsers found for file: " + fileName +
                            " -> " + matchingParsers.stream()
                            .map(p -> p.getClass().getSimpleName())
                            .toList()
            );
        }
    }

    public void validateStartNode(Map<String, DialogNode> dialogMap, String fileName) {
        if (!dialogMap.containsKey(StartCommand.COMMAND_NAME)) {
            throw new DialogLoadingException("Dialog must contain '/start' node in file: " + fileName);
        }
    }

    public void validateMediaResource(URL resourceUrl, String fileName) {
        if (resourceUrl == null) {
            throw new DialogLoadingException("Media file not found: " + fileName);
        }
    }

    public void validateButtons(DialogNode node) {
        if (node.buttons() == null || node.buttons().isEmpty()) return;

        ButtonType type = node.buttonType();
        for (Button button : node.buttons()) {
            validateButton(button, type);
        }
    }

    private void validateButton(Button button, ButtonType type) {
        String url  = button.getUrl();
        String next = button.getNext();
        boolean hasUrl  = url != null && !url.isBlank();
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
