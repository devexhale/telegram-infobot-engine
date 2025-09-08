package com.jawisimo.tbcfstarter.validator;

import com.jawisimo.tbcfstarter.exception.DialogLoadingException;
import com.jawisimo.tbcfstarter.exception.MediaLoadingException;
import com.jawisimo.tbcfstarter.handler.ContentHandler;
import com.jawisimo.tbcfstarter.model.Button;
import com.jawisimo.tbcfstarter.model.ButtonType;
import com.jawisimo.tbcfstarter.model.ContentNode;
import com.jawisimo.tbcfstarter.model.DialogNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentValidator {

    private final List<ContentHandler> contentHandlers;

    public void validateContentNode(ContentNode contentNode) {
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
        } else { // INLINE
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
