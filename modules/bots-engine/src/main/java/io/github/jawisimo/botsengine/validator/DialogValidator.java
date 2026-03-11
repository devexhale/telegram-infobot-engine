package io.github.jawisimo.botsengine.validator;

import io.github.jawisimo.botsengine.exception.DialogLoadingException;
import io.github.jawisimo.botsengine.interaction.command.commandset.StartCommand;
import io.github.jawisimo.botsengine.interaction.content.handler.ContentHandler;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import io.github.jawisimo.botsengine.model.*;
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
   *
   * <p>Missing navigation targets are logged as warnings.
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
      dialogMap
          .nodes()
          .forEach((key, node) -> validateNode(key, node, dialogMap, errors, warnings));
    }

    if (!warnings.isEmpty()) {
      log.warn(
          ValidationIssueFormatter.format(
              "Dialog logic validation completed with %d warnings for file '%s':"
                  .formatted(warnings.size(), fileName),
              warnings));
    }

    if (!errors.isEmpty()) {
      throw new DialogLoadingException(
          ValidationIssueFormatter.format(
              "Dialog loading failed with %d errors for file '%s':"
                  .formatted(errors.size(), fileName),
              errors));
    }
  }

  private void validateDialogMap(DialogMap dialogMap, String fileName, List<String> errors) {
    if (dialogMap == null) {
      errors.add("Dialog map is null in file '%s'".formatted(fileName));
    } else if (dialogMap.nodes().isEmpty()) {
      errors.add("Dialog map is empty in file '%s'".formatted(fileName));
    }
  }

  private void validateStartNode(DialogMap dialogMap, String fileName, List<String> errors) {
    if (!dialogMap.containsNodeKey(StartCommand.COMMAND_NAME)) {
      errors.add(
          "Dialog must contain node '%s' in file '%s'"
              .formatted(StartCommand.COMMAND_NAME, fileName));
    }
  }

  private void validateNode(
      String nodeKey,
      DialogNode node,
      DialogMap dialogMap,
      List<String> errors,
      List<String> warnings) {
    if (node == null) {
      errors.add("%s is empty".formatted(path(nodeKey)));
      return;
    }

    validateMessage(node, nodeKey, errors);
    validateButtonType(node, nodeKey, errors);
    validateButtons(node, nodeKey, dialogMap, errors, warnings);
    validateContent(node, nodeKey, errors);
  }

  private void validateMessage(DialogNode node, String nodeKey, List<String> errors) {
    if (isInvalidString(node.message())) {
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

  private void validateButton(Button button, ButtonType type, String path, List<String> errors) {
    if (button == null) {
      errors.add("%s is empty".formatted(path(path)));
      return;
    }

    if (isInvalidString(button.label())) {
      errors.add("%s is missing or blank".formatted(path(path + ".label")));
    }

    boolean hasUrl = !isInvalidString(button.url());
    boolean hasNext = !isInvalidString(button.next());

    if (type == ButtonType.REPLY) {
      if (hasUrl)
        errors.add("%s must not be present for reply button".formatted(path(path + ".url")));
      if (!hasNext)
        errors.add("%s is missing or blank for reply button".formatted(path(path + ".next")));
    } else if (type == ButtonType.INLINE) {
      if (!hasUrl && !hasNext)
        errors.add(
            "%s must contain either 'next' or 'url' for inline button".formatted(path(path)));
      if (hasUrl && hasNext)
        errors.add(
            "%s cannot contain both 'next' and 'url' for inline button".formatted(path(path)));
    }
  }

  private void validateContentNode(
      ContentNode contentNode, String contentPath, List<String> errors) {
    if (contentNode == null) {
      errors.add("%s is empty".formatted(path(contentPath)));
      return;
    }

    if (contentNode.type() == null || contentNode.type() == ContentType.UNKNOWN) {
      errors.add("%s is missing or not supported".formatted(path(contentPath + ".type")));
      return;
    }

    boolean hasText = !isInvalidString(contentNode.text());
    boolean hasMedia = contentNode.media() != null;

    if (contentNode.type() == ContentType.TEXT) {
      if (!hasText)
        errors.add(
            "%s is missing or blank for text content".formatted(path(contentPath + ".text")));
      if (hasMedia)
        errors.add("%s must not be present for text type".formatted(path(contentPath + ".media")));
    } else if (contentNode.type() == ContentType.MEDIA) {
      if (!hasMedia)
        errors.add("%s is missing for media content".formatted(path(contentPath + ".media")));
      else validateMedia(contentNode, contentNode.media(), contentPath + ".media", errors);
      if (hasText)
        errors.add("%s must not be present for media type".formatted(path(contentPath + ".text")));
    }
  }

  private void validateMedia(ContentNode node, Media media, String path, List<String> errors) {
    if (isInvalidString(media.type()))
      errors.add("%s is missing or blank".formatted(path(path + ".type")));
    if (isInvalidString(media.fileName())) {
      errors.add("%s is missing or blank".formatted(path(path + ".file_name")));
    } else {
      validateMediaFileExists(media.fileName(), path, errors);
    }

    if (media.type() != null && contentHandlers.stream().noneMatch(h -> h.canHandle(node))) {
      errors.add("%s '%s' is not supported".formatted(path(path + ".type"), media.type()));
    }
  }

  private void validateContent(DialogNode node, String nodeKey, List<String> errors) {
    if (node.content() == null) return;
    for (int i = 0; i < node.content().size(); i++) {
      validateContentNode(node.content().get(i), nodeKey + ".content[" + i + "]", errors);
    }
  }

  private void validateMissingNextNode(
      Button btn, DialogMap map, String path, List<String> warnings) {
    if (btn != null && !isInvalidString(btn.next()) && !map.containsNodeKey(btn.next())) {
      warnings.add("%s points to missing node '%s'".formatted(path(path + ".next"), btn.next()));
    }
  }

  private void validateMediaFileExists(String fileName, String mediaPath, List<String> errors) {
    String resourcePath = Paths.get(MEDIA_FOLDER, fileName).toString();
    if (getClass().getClassLoader().getResource(resourcePath) == null) {
      errors.add(
          "%s points to missing media file '%s/%s'"
              .formatted(path(mediaPath + ".file_name"), MEDIA_FOLDER, fileName));
    }
  }

  private boolean isInvalidString(String s) {
    return s == null || s.isBlank();
  }

  private String path(String value) {
    return "Node '%s'".formatted(value);
  }
}
