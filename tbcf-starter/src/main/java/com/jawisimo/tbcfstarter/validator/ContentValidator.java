package com.jawisimo.tbcfstarter.validator;

import com.jawisimo.tbcfstarter.exception.MediaLoadingException;
import com.jawisimo.tbcfstarter.handler.ContentHandler;
import com.jawisimo.tbcfstarter.model.ContentNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentValidator {

    private final List<ContentHandler> contentHandlers;

    public void validate(ContentNode contentNode) {
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
