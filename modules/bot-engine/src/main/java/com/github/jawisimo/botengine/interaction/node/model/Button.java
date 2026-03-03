package com.github.jawisimo.botengine.interaction.node.model;

import com.github.jawisimo.botengine.exception.DialogLoadingException;

/**
 * Represents a dialog button defined in configuration.
 *
 * <p>Encapsulates button label and navigation metadata such as next node or external URL.
 *
 * @param label button display text
 * @param next ID of the next dialog node to navigate to
 * @param url external URL to open (optional, mutually exclusive with next)
 * @since 1.0
 */
public record Button(String label, String next, String url) {

  /**
   * Validates required button fields.
   *
   * @throws DialogLoadingException if the label is missing or blank
   */
  public Button {
    if (label == null || label.isBlank()) {
      throw new DialogLoadingException("Button label is missing or blank");
    }
  }
}
