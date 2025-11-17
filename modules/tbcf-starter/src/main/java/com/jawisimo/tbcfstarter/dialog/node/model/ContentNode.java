package com.jawisimo.tbcfstarter.dialog.node.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ContentNode {
    private ContentType type;
    private String text;
    private Media media;
}
