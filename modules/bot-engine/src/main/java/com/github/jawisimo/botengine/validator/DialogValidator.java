package com.github.jawisimo.botengine.validator;

import com.github.jawisimo.botengine.exception.DialogLoadingException;
import com.github.jawisimo.botengine.interaction.command.commandset.StartCommand;
import com.github.jawisimo.botengine.interaction.media.handler.MediaHandler;
import java.util.List;

import com.github.jawisimo.botengine.interaction.node.model.*;
import org.springframework.stereotype.Component;

@Component
public class DialogValidator {

  public void validateStartNode(DialogMap dialogMap, String fileName) {
    if (!dialogMap.containsNodeKey(StartCommand.COMMAND_NAME)) {
      throw new DialogLoadingException("Dialog must contain '/start' node in file: " + fileName);
    }
  }

  public void validateContent(ContentNode contentNode, List<MediaHandler> mediaHandlers) {
    if (contentNode.type() == ContentType.TEXT
        && (contentNode.text() == null || contentNode.text().isEmpty())) {
      throw new DialogLoadingException("Content text is null or empty, but content type is TEXT");
    }

    if (contentNode.media() != null) {
      validateMedia(contentNode.media(), contentNode, mediaHandlers);
    }
  }

  public void validateButtons(DialogNode node) {
    ButtonType type = node.buttonType();

    for (Button button : node.buttons()) {
      validateButton(button, type);
    }
  }

  private void validateMedia(
      Media media, ContentNode contentNode, List<MediaHandler> mediaHandlers) {
    String type = media.type();
    String fileName = media.fileName();

    boolean supported = mediaHandlers.stream().anyMatch(h -> h.canHandle(contentNode));

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
