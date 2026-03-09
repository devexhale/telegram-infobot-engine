package com.github.jawisimo.botengine.model;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Defines the type of content inside a dialog node.
 *
 * <p>TEXT represents plain text content.
 *
 * <p>MEDIA represents media content such as photo, audio, or video.
 *
 * @since 1.0
 */
public enum ContentType {
  TEXT,
  MEDIA;

  /**
   * Converts a string value to {@link ContentType}.
   *
   * <p>Returns {@code null} when the value is missing or invalid.
   *
   * @param value the raw content type value
   * @return the resolved content type
   */
  @JsonCreator
  public static ContentType fromString(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }

    try {
      return ContentType.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
}
