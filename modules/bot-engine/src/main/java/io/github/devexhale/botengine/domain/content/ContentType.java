package io.github.devexhale.botengine.domain.content;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Locale;

/**
 * Defines the type of content inside a definition node.
 *
 * <p>{@link #UNKNOWN} is used during validation to produce a meaningful error message when the
 * provided value does not match any known type.
 *
 * @since 1.0
 */
public enum ContentType {
  TEXT,
  PHOTO,
  AUDIO,
  VIDEO,
  DOCUMENT,
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
      return ContentType.valueOf(value.toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException e) {
      return UNKNOWN;
    }
  }
}
