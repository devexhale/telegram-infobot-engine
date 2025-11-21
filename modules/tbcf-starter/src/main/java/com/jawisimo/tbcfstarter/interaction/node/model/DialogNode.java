package com.jawisimo.tbcfstarter.interaction.node.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jawisimo.tbcfstarter.exception.DialogLoadingException;

import java.util.List;

public record DialogNode(
        List<ContentNode> content,
        String message,
        @JsonProperty("button_type") ButtonType buttonType,
        List<Button> buttons) {

    public DialogNode {
        if (buttons == null || buttons.isEmpty()) {
            throw new DialogLoadingException("Buttons list are required and cannot be null or empty");
        }

        if (message == null || message.isBlank()) {
            throw new DialogLoadingException("Field 'message' is required and cannot be null or blank");
        }
    }
}
