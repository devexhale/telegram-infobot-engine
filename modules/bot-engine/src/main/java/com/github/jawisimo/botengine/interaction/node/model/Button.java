package com.github.jawisimo.botengine.interaction.node.model;

import com.github.jawisimo.botengine.exception.DialogLoadingException;

public record Button(String label, String next, String url) {

  public Button {
    if (label == null || label.isBlank()) {
      throw new DialogLoadingException("Button label is missing or blank");
    }
  }
}
