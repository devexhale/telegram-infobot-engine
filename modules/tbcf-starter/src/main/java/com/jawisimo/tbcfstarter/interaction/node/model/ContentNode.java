package com.jawisimo.tbcfstarter.interaction.node.model;

import com.jawisimo.tbcfstarter.exception.DialogLoadingException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record ContentNode(ContentType type, String text, Media media) {

    public ContentNode {
        if (type == null) {
            throw new DialogLoadingException("Content type is missing or not valid");
        }
    }
}
