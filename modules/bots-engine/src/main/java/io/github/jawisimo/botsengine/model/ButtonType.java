package io.github.jawisimo.botsengine.model;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Defines the keyboard type used by a dialog node.
 *
 * <p>INLINE creates inline keyboard buttons.
 *
 * <p>REPLY creates reply keyboard buttons.
 *
 * <p>UNKNOWN represents an invalid value during deserialization.
 *
 * @since 1.0
 */
public enum ButtonType {
  INLINE,
  REPLY,
  UNKNOWN;

  /**
   * Converts a string value to {@link ButtonType}.
   *
   * <p>Returns {@code null} when the value is missing.
   *
   * <p>Returns {@link ButtonType#UNKNOWN} for invalid values.
   *
   * @param value the raw button type value
   * @return the resolved button type
   */
  @JsonCreator
  public static ButtonType fromString(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }

    try {
      return ButtonType.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      return UNKNOWN;
    }
  }
}
