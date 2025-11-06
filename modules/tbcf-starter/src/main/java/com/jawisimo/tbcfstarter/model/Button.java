package com.jawisimo.tbcfstarter.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Button  {
    private String label;
    private String next;
    private String url;
}
