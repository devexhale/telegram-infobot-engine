package com.jawisimo.tbcfstarter.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record DialogNode(
        List<ContentNode> content,
        String question,
        @JsonProperty("button_type") ButtonType buttonType,
        List<Button> buttons) {

    public DialogNode {
        if (buttons == null || buttons.isEmpty()) {
            throw new IllegalArgumentException("Buttons list cannot be null or empty");
        }

        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("Field 'question' is required and cannot be null or blank");
        }
    }
}
