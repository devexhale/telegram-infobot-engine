package io.github.devexhale.botengine.domain.dialog;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Locale;

/**
 * Defines the keyboard type used by a dialog node.
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
   * <p>Returns {@link ButtonType#UNKNOWN} when the value is present but does not match any known
   * type.
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
      return ButtonType.valueOf(value.toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException e) {
      return UNKNOWN;
    }
  }
}
