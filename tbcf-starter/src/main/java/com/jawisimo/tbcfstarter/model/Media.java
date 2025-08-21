package com.jawisimo.tbcfstarter.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Media {
    private String type;
    private String fileName;
    private String caption;
}
