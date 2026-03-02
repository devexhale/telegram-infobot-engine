package com.github.jawisimo.botengine.interaction.node.model;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Defines the supported button rendering types in a dialog node.
 *
 * <p>INLINE buttons are displayed within the message, while REPLY buttons are shown as reply
 * keyboard options.
 *
 * @since 1.0
 */
public enum ButtonType {
  INLINE,
  REPLY;

  /**
   * Creates a {@link ButtonType} from a case-insensitive string value.
   *
   * @param value the button type value
   * @return the corresponding {@link ButtonType}, or {@code null} if input is null
   */
  @JsonCreator
  public static ButtonType fromString(String value) {
    if (value == null) {
      return null;
    }
    return ButtonType.valueOf(value.toUpperCase());
  }
}
