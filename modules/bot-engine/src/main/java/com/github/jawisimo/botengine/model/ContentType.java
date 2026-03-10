package com.github.jawisimo.botengine.model;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Defines the type of content inside a dialog node.
 *
 * <p>TEXT represents plain text content.
 *
 * <p>MEDIA represents media content such as photo, audio, or video.
 *
 * <p>UNKNOWN represents an unrecognized content type returned when the provided value does not
 * match any known type. Used during validation to produce a meaningful error message.
 *
 * @since 1.0
 */
public enum ContentType {
  TEXT,
  MEDIA,
  UNKNOWN;

  /**
   * Converts a string value to {@link ContentType}.
   *
   * <p>Returns {@code null} when the value is missing or blank.
   *
   * <p>Returns {@link #UNKNOWN} when the value is present but does not match any known type.
   *
   * @param value the raw content type value
   * @return the resolved content type, {@code null} if blank, or {@link #UNKNOWN} if unrecognized
   */
  @JsonCreator
  public static ContentType fromString(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }

    try {
      return ContentType.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      return UNKNOWN;
    }
  }
}
