package com.jawisimo.tbcfstarter.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DialogNode {

    private String message;

    private ButtonType buttonType;

    private Media media;

    private List<Button> buttons;
}
