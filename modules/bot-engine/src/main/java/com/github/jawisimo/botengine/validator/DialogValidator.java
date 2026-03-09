package com.github.jawisimo.botengine.validator;

import com.github.jawisimo.botengine.exception.DialogLoadingException;
import com.github.jawisimo.botengine.interaction.command.commandset.StartCommand;
import com.github.jawisimo.botengine.interaction.content.handler.ContentHandler;
import com.github.jawisimo.botengine.model.Button;
import com.github.jawisimo.botengine.model.ButtonType;
import com.github.jawisimo.botengine.model.ContentNode;
import com.github.jawisimo.botengine.model.ContentType;
import com.github.jawisimo.botengine.model.DialogMap;
import com.github.jawisimo.botengine.model.DialogNode;
import com.github.jawisimo.botengine.model.Media;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Validates dialog definitions after parsing and before runtime execution.
 *
 * <p>Performs structural validation for the entire dialog tree and collects all detected errors
 * before throwing a single {@link DialogLoadingException}.
 *
 * <p>Also checks dialog navigation targets. Missing target nodes are reported as warnings and do
 * not prevent application startup.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DialogValidator {

  private static final String MEDIA_FOLDER = "media";

  private final List<ContentHandler> contentHandlers;

  /**
   * Validates the entire dialog map.
   *
   * <p>Structural problems are collected and thrown as a single {@link DialogLoadingException}.
   * Missing navigation targets are logged as warnings.
   *
   * @param dialogMap the parsed dialog map
   * @param fileName the dialog file name
   * @throws DialogLoadingException if structural validation fails
   */
  public void validate(DialogMap dialogMap, String fileName) {
    List<String> errors = new ArrayList<>();
    List<String> warnings = new ArrayList<>();

    validateDialogMap(dialogMap, fileName, errors);

    if (dialogMap != null) {
      validateStartNode(dialogMap, fileName, errors);

      for (Map.Entry<String, DialogNode> entry : dialogMap.nodes().entrySet()) {
        validateNode(entry.getKey(), entry.getValue(), dialogMap, errors, warnings);
      }
    }

    if (!warnings.isEmpty()) {
      log.error(
          ValidationErrorFormatter.format(
              "Dialog logic validation completed with %d warnings for file '%s':"
                  .formatted(warnings.size(), fileName),
              warnings));
    }

    if (!errors.isEmpty()) {
      throw new DialogLoadingException(
          ValidationErrorFormatter.format(
              "Dialog loading failed with %d errors for file '%s':"
                  .formatted(errors.size(), fileName),
              errors));
    }
  }

  private void validateDialogMap(DialogMap dialogMap, String fileName, List<String> errors) {
    if (dialogMap == null) {
      errors.add("Dialog map is null in file '%s'".formatted(fileName));
      return;
    }

    if (dialogMap.nodes().isEmpty()) {
      errors.add("Dialog map is empty in file '%s'".formatted(fileName));
    }
  }

  private void validateStartNode(DialogMap dialogMap, String fileName, List<String> errors) {
    if (!dialogMap.containsNodeKey(StartCommand.COMMAND_NAME)) {
      errors.add("Dialog must contain node '/start' in file '%s'".formatted(fileName));
    }
  }

  private void validateNode(
      String nodeKey,
      DialogNode node,
      DialogMap dialogMap,
      List<String> errors,
      List<String> warnings) {

    if (node == null) {
      errors.add("%s is null".formatted(path(nodeKey)));
      return;
    }

    validateContent(node, nodeKey, errors);
    validateMessage(node, nodeKey, errors);
    validateButtonType(node, nodeKey, errors);
    validateButtons(node, nodeKey, dialogMap, errors, warnings);
  }

  private void validateMessage(DialogNode node, String nodeKey, List<String> errors) {
    if (node.message() == null || node.message().isBlank()) {
      errors.add("%s is missing or blank".formatted(path(nodeKey + ".message")));
    }
  }

  private void validateButtonType(DialogNode node, String nodeKey, List<String> errors) {
    if (node.buttonType() == ButtonType.UNKNOWN) {
      errors.add("%s is not valid".formatted(path(nodeKey + ".button_type")));
    }
  }

  private void validateButtons(
      DialogNode node,
      String nodeKey,
      DialogMap dialogMap,
      List<String> errors,
      List<String> warnings) {

    if (node.buttons() == null || node.buttons().isEmpty()) {
      errors.add("%s is missing or empty".formatted(path(nodeKey + ".buttons")));
      return;
    }

    for (int i = 0; i < node.buttons().size(); i++) {
      Button button = node.buttons().get(i);
      String buttonPath = nodeKey + ".buttons[" + i + "]";

      validateButton(button, node.buttonType(), buttonPath, errors);
      validateMissingNextNode(button, dialogMap, buttonPath, warnings);
    }
  }

  private void validateButton(
      Button button, ButtonType buttonType, String buttonPath, List<String> errors) {
    if (button == null) {
      errors.add("%s is null".formatted(path(buttonPath)));
      return;
    }

    if (button.label() == null || button.label().isBlank()) {
      errors.add("%s is missing or blank".formatted(path(buttonPath + ".label")));
    }

    boolean hasUrl = button.url() != null && !button.url().isBlank();
    boolean hasNext = button.next() != null && !button.next().isBlank();

    if (buttonType == null) {
      return;
    }

    if (buttonType == ButtonType.REPLY) {
      validateReplyButton(buttonPath, hasUrl, hasNext, errors);
    } else if (buttonType == ButtonType.INLINE) {
      validateInlineButton(buttonPath, hasUrl, hasNext, errors);
    }
  }

  private void validateReplyButton(
      String buttonPath, boolean hasUrl, boolean hasNext, List<String> errors) {
    if (hasUrl) {
      errors.add("%s must not be present for reply button".formatted(path(buttonPath + ".url")));
    }

    if (!hasNext) {
      errors.add("%s is missing or blank for reply button".formatted(path(buttonPath + ".next")));
    }
  }

  private void validateInlineButton(
      String buttonPath, boolean hasUrl, boolean hasNext, List<String> errors) {
    if (!hasUrl && !hasNext) {
      errors.add("%s must contain either 'next' or 'url'".formatted(path(buttonPath)));
    }

    if (hasUrl && hasNext) {
      errors.add("%s cannot contain both 'next' and 'url'".formatted(path(buttonPath)));
    }
  }

  private void validateMissingNextNode(
      Button button, DialogMap dialogMap, String buttonPath, List<String> warnings) {
    if (button == null || button.next() == null || button.next().isBlank()) {
      return;
    }

    if (!dialogMap.containsNodeKey(button.next())) {
      warnings.add(
          "%s points to missing node '%s'".formatted(path(buttonPath + ".next"), button.next()));
    }
  }

  private void validateContent(DialogNode node, String nodeKey, List<String> errors) {
    if (node.content() == null || node.content().isEmpty()) {
      return;
    }

    for (int i = 0; i < node.content().size(); i++) {
      ContentNode contentNode = node.content().get(i);
      String contentPath = nodeKey + ".content[" + i + "]";

      validateContentNode(contentNode, contentPath, errors);
    }
  }

  private void validateContentNode(
      ContentNode contentNode, String contentPath, List<String> errors) {
    if (contentNode == null) {
      errors.add("%s is null".formatted(path(contentPath)));
      return;
    }

    if (contentNode.type() == null) {
      errors.add("%s is missing or not valid".formatted(path(contentPath + ".type")));
      return;
    }

    boolean hasText = contentNode.text() != null && !contentNode.text().isBlank();
    boolean hasMedia = contentNode.media() != null;

    if (contentNode.type() == ContentType.TEXT) {
      validateTextContent(contentPath, hasText, hasMedia, errors);
      return;
    }

    if (contentNode.type() == ContentType.MEDIA) {
      validateMediaContent(contentNode, contentPath, hasText, hasMedia, errors);
    }
  }

  private void validateTextContent(
      String contentPath, boolean hasText, boolean hasMedia, List<String> errors) {
    if (!hasText) {
      errors.add("%s is missing or blank for text content".formatted(path(contentPath + ".text")));
    }

    if (hasMedia) {
      errors.add(
          "%s must not be present when type is 'text'".formatted(path(contentPath + ".media")));
    }
  }

  private void validateMediaContent(
      ContentNode contentNode,
      String contentPath,
      boolean hasText,
      boolean hasMedia,
      List<String> errors) {
    if (!hasMedia) {
      errors.add("%s is missing for media content".formatted(path(contentPath + ".media")));
      return;
    }

    if (hasText) {
      errors.add(
          "%s must not be present when type is 'media'".formatted(path(contentPath + ".text")));
    }

    validateMedia(contentNode, contentNode.media(), contentPath + ".media", errors);
  }

  private void validateMedia(
      ContentNode contentNode, Media media, String mediaPath, List<String> errors) {
    if (media == null) {
      errors.add("%s is null".formatted(path(mediaPath)));
      return;
    }

    if (media.type() == null || media.type().isBlank()) {
      errors.add("%s is missing or blank".formatted(path(mediaPath + ".type")));
    }

    if (media.fileName() == null || media.fileName().isBlank()) {
      errors.add("%s is missing or blank".formatted(path(mediaPath + ".file_name")));
      return;
    }

    if (media.type() != null) {
      boolean supported =
          contentHandlers.stream().anyMatch(handler -> handler.canHandle(contentNode));
      if (!supported) {
        errors.add("%s '%s' is not supported".formatted(path(mediaPath + ".type"), media.type()));
      }
    }

    validateMediaFileExists(media.fileName(), mediaPath, errors);
  }

  private void validateMediaFileExists(String fileName, String mediaPath, List<String> errors) {
    String resourcePath = Paths.get(MEDIA_FOLDER, fileName).toString();
    boolean exists = getClass().getClassLoader().getResource(resourcePath) != null;

    if (!exists) {
      errors.add(
          "%s points to missing media file '%s/%s'"
              .formatted(path(mediaPath + ".file_name"), MEDIA_FOLDER, fileName));
    }
  }

  private String path(String value) {
    return "Node '%s'".formatted(value);
  }
}
