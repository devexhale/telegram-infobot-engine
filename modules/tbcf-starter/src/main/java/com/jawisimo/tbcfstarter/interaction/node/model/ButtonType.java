package com.jawisimo.tbcfstarter.interaction.node.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ButtonType {
    INLINE,
    REPLY;

    @JsonCreator
    public static ButtonType fromString(String value) {
        if (value == null) return null;
        return ButtonType.valueOf(value.toUpperCase());
    }
}
