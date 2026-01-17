package com.jawisimo.tbcfstarter.interaction.node.model;

import com.jawisimo.tbcfstarter.exception.DialogLoadingException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record Button(String label, String next, String url) {

    public Button {
        if (label == null || label.isBlank()) {
            throw new DialogLoadingException("Button label is missing or blank");
        }
    }
}
