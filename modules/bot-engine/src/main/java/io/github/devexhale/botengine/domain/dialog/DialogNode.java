package io.github.devexhale.botengine.domain.dialog;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.devexhale.botengine.domain.content.ContentNode;

import java.util.List;

/**
 * Represents a dialog node in the dialog tree.
 *
 * @param content the list of content elements displayed before the message
 * @param message the main message text of the node
 * @param buttonType the keyboard type used for the node buttons
 * @param buttons the list of buttons available in the node
 * @since 1.0
 */
public record DialogNode(
    List<ContentNode> content,
    String message,
    @JsonProperty("button_type") ButtonType buttonType,
    List<Button> buttons) {}
