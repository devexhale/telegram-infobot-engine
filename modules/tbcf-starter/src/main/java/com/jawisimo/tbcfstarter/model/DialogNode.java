package com.jawisimo.tbcfstarter.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jawisimo.tbcfstarter.exception.DialogLoadingException;

import java.util.List;

public record DialogNode(
        List<ContentNode> content,
        String prompt,
        @JsonProperty("button_type") ButtonType buttonType,
        List<Button> buttons) {

    public DialogNode {
        if (buttons == null || buttons.isEmpty()) {
            throw new DialogLoadingException("Buttons list are required and cannot be null or empty");
        }

        if (prompt == null || prompt.isBlank()) {
            throw new DialogLoadingException("Field 'prompt' is required and cannot be null or blank");
        }
    }
}
