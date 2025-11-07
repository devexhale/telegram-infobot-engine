package com.jawisimo.tbcfstarter.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Media {
    private String type;

    @JsonProperty("file_name")
    private String fileName;

    private String caption;
}
