package com.github.jawisimo.botengine.validator;

import com.github.jawisimo.botengine.exception.DialogLoadingException;
import com.github.jawisimo.botengine.interaction.command.commandset.StartCommand;
import com.github.jawisimo.botengine.interaction.content.handler.ContentHandler;
import com.github.jawisimo.botengine.interaction.node.model.Button;
import com.github.jawisimo.botengine.interaction.node.model.ButtonType;
import com.github.jawisimo.botengine.interaction.node.model.ContentNode;
import com.github.jawisimo.botengine.interaction.node.model.ContentType;
import com.github.jawisimo.botengine.interaction.node.model.DialogMap;
import com.github.jawisimo.botengine.interaction.node.model.DialogNode;
import com.github.jawisimo.botengine.interaction.node.model.Media;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Validates structural and business rules of dialog definitions.
 *
 * <p>Performs checks for required nodes and verifies content, media, and button configuration
 * consistency. Throws {@link DialogLoadingException} when a validation rule is violated.
 *
 * @since 1.0
 */
@Component
public class DialogValidator {

  /**
   * Verifies that the dialog contains the required {@code /start} node.
   *
   * @param dialogMap the parsed dialog map
   * @param fileName the source dialog file name
   * @throws DialogLoadingException if the start node is missing
   */
  public void validateStartNode(DialogMap dialogMap, String fileName) {
    if (!dialogMap.containsNodeKey(StartCommand.COMMAND_NAME)) {
      throw new DialogLoadingException("Dialog must contain '/start' node in file: " + fileName);
    }
  }

  /**
   * Validates a content node against basic invariants and available handlers.
   *
   * @param contentNode the content node to validate
   * @param contentHandlers the available content handlers
   * @throws DialogLoadingException if the content node is invalid or unsupported
   */
  public void validateContent(ContentNode contentNode, List<ContentHandler> contentHandlers) {
    if (contentNode.type() == ContentType.TEXT
        && (contentNode.text() == null || contentNode.text().isEmpty())) {
      throw new DialogLoadingException("Content text is null or empty, but content type is TEXT");
    }

    if (contentNode.media() != null) {
      validateMedia(contentNode.media(), contentNode, contentHandlers);
    }
  }

  /**
   * Validates button constraints for the given dialog node.
   *
   * @param node the dialog node to validate
   * @throws DialogLoadingException if any button violates constraints for the node button type
   */
  public void validateButtons(DialogNode node) {
    ButtonType type = node.buttonType();
    for (Button button : node.buttons()) {
      validateButton(button, type);
    }
  }

  private void validateMedia(
      Media media, ContentNode contentNode, List<ContentHandler> contentHandlers) {
    String type = media.type();
    String fileName = media.fileName();

    boolean supported = contentHandlers.stream().anyMatch(h -> h.canHandle(contentNode));
    if (!supported) {
      throw new DialogLoadingException(
          "No handler found for media type: " + type + " (file: " + fileName + ")");
    }
  }

  private void validateButton(Button button, ButtonType type) {
    String url = button.url();
    String next = button.next();
    boolean hasUrl = url != null && !url.isBlank();
    boolean hasNext = next != null && !next.isBlank();

    if (type == ButtonType.REPLY) {
      validateReplyButton(hasUrl, hasNext);
    } else {
      validateInlineButton(hasUrl, hasNext);
    }
  }

  private void validateReplyButton(boolean hasUrl, boolean hasNext) {
    if (hasUrl) {
      throw new DialogLoadingException("Reply button cannot have a 'URL'");
    }

    if (!hasNext) {
      throw new DialogLoadingException("Reply button must have a 'next' (callback text)");
    }
  }

  private void validateInlineButton(boolean hasUrl, boolean hasNext) {
    if (!hasUrl && !hasNext) {
      throw new DialogLoadingException("Inline button must have either 'next' or 'URL'");
    }

    if (hasUrl && hasNext) {
      throw new DialogLoadingException("Inline button cannot have both 'next' and 'URL'");
    }
  }
}
