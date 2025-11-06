package com.jawisimo.tbcfstarter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentNode {
    private ContentType type;
    private String text;
    private Media media;
}
