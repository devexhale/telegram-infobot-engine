package com.github.jawisimo.botengine.interaction.node.model;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Defines supported content types for a dialog node.
 *
 * <p>TEXT represents plain textual content, while MEDIA represents content accompanied by a media
 * file.
 *
 * @since 1.0
 */
public enum ContentType {
  TEXT,
  MEDIA;

  /**
   * Creates a {@link ContentType} from a case-insensitive string value.
   *
   * @param value the content type value
   * @return the corresponding {@link ContentType}, or {@code null} if input is null
   */
  @JsonCreator
  public static ContentType fromString(String value) {
    if (value == null) {
      return null;
    }
    return ContentType.valueOf(value.toUpperCase());
  }
}
