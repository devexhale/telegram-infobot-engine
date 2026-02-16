package com.jawisimo.tbcfstarter.interaction.node.model;

import com.jawisimo.tbcfstarter.exception.DialogLoadingException;

public record ContentNode(ContentType type, String text, Media media) {

  public ContentNode {
    if (type == null) {
      throw new DialogLoadingException("Content type is missing or not valid");
    }
  }
}
