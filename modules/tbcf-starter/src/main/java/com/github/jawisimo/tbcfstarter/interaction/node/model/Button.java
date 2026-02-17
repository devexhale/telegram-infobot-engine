package com.github.jawisimo.tbcfstarter.interaction.node.model;

import com.github.jawisimo.tbcfstarter.exception.DialogLoadingException;

public record Button(String label, String next, String url) {

  public Button {
    if (label == null || label.isBlank()) {
      throw new DialogLoadingException("Button label is missing or blank");
    }
  }
}
