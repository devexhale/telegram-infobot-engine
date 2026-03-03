package com.github.jawisimo.botengine.interaction.node.model;

import com.github.jawisimo.botengine.exception.DialogLoadingException;

/**
 * Represents the content definition of a dialog node.
 *
 * <p>Contains content type and optional text or media configuration.
 *
 * @param type content type (text, photo, video, etc.)
 * @param text message text (optional, used when type is text-based)
 * @param media media configuration (optional, used when type is media-based)
 * @since 1.0
 */
public record ContentNode(ContentType type, String text, Media media) {

  /**
   * Validates required content fields.
   *
   * @throws DialogLoadingException if the content type is missing
   */
  public ContentNode {
    if (type == null) {
      throw new DialogLoadingException("Content type is missing or not valid");
    }
  }
}
