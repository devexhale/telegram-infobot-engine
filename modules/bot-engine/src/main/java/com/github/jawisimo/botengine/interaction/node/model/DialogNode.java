package com.github.jawisimo.botengine.interaction.node.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jawisimo.botengine.exception.DialogLoadingException;
import com.github.jawisimo.botengine.validator.ValidationErrorFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a dialog node containing message content and interaction options.
 *
 * <p>Defines the message text, associated content, button type, and navigation buttons.
 *
 * @param content list of additional content nodes (optional, e.g. photos, videos)
 * @param message main message text displayed to the user
 * @param buttonType layout type for the buttons
 * @param buttons list of buttons for user interaction
 * @since 1.0
 */
public record DialogNode(
    List<ContentNode> content,
    String message,
    @JsonProperty("button_type") ButtonType buttonType,
    List<Button> buttons) {

  /**
   * Validates required dialog node fields.
   *
   * @throws DialogLoadingException if mandatory fields are missing or invalid
   */
  public DialogNode {
    List<String> errors = new ArrayList<>();

    if (message == null || message.isBlank()) {
      errors.add("Field 'message' is missing or blank, but it is required");
    }

    if (buttons == null || buttons.isEmpty()) {
      errors.add("Buttons list is missing or empty, but it is required");
    }

    if (!errors.isEmpty()) {
      String errorMessage = ValidationErrorFormatter.format("Dialog node loading failed", errors);
      throw new DialogLoadingException(errorMessage);
    }
  }
}
