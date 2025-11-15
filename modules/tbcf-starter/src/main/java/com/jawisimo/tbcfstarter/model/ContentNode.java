package com.jawisimo.tbcfstarter.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ContentNode {
    private ContentType type;
    private String text;
    private Media media;
}
