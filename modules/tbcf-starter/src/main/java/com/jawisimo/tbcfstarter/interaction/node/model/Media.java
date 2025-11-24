package com.jawisimo.tbcfstarter.interaction.node.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Media(String type,
                    @JsonProperty("file_name") String fileName,
                    String caption) {
}
