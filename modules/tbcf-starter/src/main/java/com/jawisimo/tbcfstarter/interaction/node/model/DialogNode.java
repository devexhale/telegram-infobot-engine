package com.jawisimo.tbcfstarter.interaction.node.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jawisimo.tbcfstarter.exception.DialogLoadingException;
import com.jawisimo.tbcfstarter.validator.ValidationErrorFormatter;

import java.util.ArrayList;
import java.util.List;

public record DialogNode(
        List<ContentNode> content,
        String message,
        @JsonProperty("button_type") ButtonType buttonType,
        List<Button> buttons) {

    public DialogNode {
        List<String> errors = new ArrayList<>();

        if (buttons == null || buttons.isEmpty()) {
            errors.add("Buttons list is missing or empty, but it is required");
        }

        if (message == null || message.isBlank()) {
            errors.add("Field 'message' is missing or blank, but it is required");
        }

        if (!errors.isEmpty()) {
            String errorMessage = ValidationErrorFormatter.format(
                    "Dialog node loading failed", errors
            );
            throw new DialogLoadingException(errorMessage);
        }
    }
}
