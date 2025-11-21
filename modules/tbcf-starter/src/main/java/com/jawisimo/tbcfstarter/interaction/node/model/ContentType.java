package com.jawisimo.tbcfstarter.interaction.node.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ContentType {
    TEXT,
    MEDIA;

    @JsonCreator
    public static ContentType fromString(String value) {
        if (value == null) return null;
        return ContentType.valueOf(value.toUpperCase());
    }
}
