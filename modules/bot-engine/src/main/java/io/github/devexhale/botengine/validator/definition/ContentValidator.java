package io.github.devexhale.botengine.validator.definition;

import io.github.devexhale.botengine.domain.content.ContentNode;
import io.github.devexhale.botengine.domain.content.ContentType;
import java.nio.file.Paths;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ContentValidator {

  private static final String MEDIA_FOLDER = "content";

  void validate(List<ContentNode> content, String contentPath, ValidationContext context) {
    if (content == null) {
      return;
    }

    for (int i = 0; i < content.size(); i++) {
      validateContentNode(content.get(i), contentPath + "[" + i + "]", context);
    }
  }

  private void validateContentNode(
      ContentNode node, String contentPath, ValidationContext context) {

    if (node == null) {
      context.addError("%s is empty".formatted(path(contentPath)));
      return;
    }

    if (node.type() == null || node.type() == ContentType.UNKNOWN) {
      context.addError("%s is missing or not supported".formatted(path(contentPath + ".type")));
      return;
    }

    boolean hasText = isValidString(node.text());
    boolean hasFileName = isValidString(node.fileName());
    boolean hasCaption = isValidString(node.caption());

    if (node.type() == ContentType.TEXT) {
      validateTextContent(hasText, hasFileName, hasCaption, contentPath, context);
      return;
    }

    validateMediaContent(hasText, hasFileName, node.fileName(), contentPath, context);
  }

  private void validateTextContent(
      boolean hasText,
      boolean hasFileName,
      boolean hasCaption,
      String contentPath,
      ValidationContext context) {

    if (!hasText) {
      context.addError(
          "%s is missing or blank for text content".formatted(path(contentPath + ".text")));
    }

    if (hasFileName) {
      context.addWarning(
          "%s should not be present for text type".formatted(path(contentPath + ".file_name")));
    }

    if (hasCaption) {
      context.addWarning(
          "%s should not be present for text type".formatted(path(contentPath + ".caption")));
    }
  }

  private void validateMediaContent(
      boolean hasText,
      boolean hasFileName,
      String fileName,
      String contentPath,
      ValidationContext context) {

    if (!hasFileName) {
      context.addError(
          "%s is missing or blank for media content".formatted(path(contentPath + ".file_name")));
    } else {
      validateMediaFileExists(fileName, contentPath, context);
    }

    if (hasText) {
      context.addWarning(
          "%s should not be present for media type".formatted(path(contentPath + ".text")));
    }
  }

  private void validateMediaFileExists(
      String fileName, String contentPath, ValidationContext context) {

    String resourcePath = Paths.get(MEDIA_FOLDER, fileName).toString();

    if (getClass().getClassLoader().getResource(resourcePath) == null) {
      context.addError(
          "%s points to missing media file: %s/%s"
              .formatted(path(contentPath + ".file_name"), MEDIA_FOLDER, fileName));
    }
  }

  private boolean isValidString(String value) {
    return value != null && !value.isBlank();
  }

  private String path(String value) {
    return "Node '%s'".formatted(value);
  }
}
