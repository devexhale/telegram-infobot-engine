package com.github.jawisimo.tbcfstarter.interaction.node.model;

import com.github.jawisimo.tbcfstarter.exception.DialogLoadingException;

public record ContentNode(ContentType type, String text, Media media) {

  public ContentNode {
    if (type == null) {
      throw new DialogLoadingException("Content type is missing or not valid");
    }
  }
}
