package io.github.devexhale.botengine.validator.definition;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.devexhale.botengine.domain.content.ContentNode;
import io.github.devexhale.botengine.domain.content.ContentType;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContentValidatorTest {

  private static final String CONTENT_PATH = "content";
  private static final String VALID_FILE_NAME = "Qstart.jpg";
  private static final String INVALID_FILE_NAME = "non_existent_file_xyz123.mp3";
  private static final String VALID_TEXT = "Hello world";
  private static final String VALID_CAPTION = "Test caption";

  private ContentValidator contentValidator;
  private ValidationContext context;

  @BeforeEach
  void setUp() {
    contentValidator = new ContentValidator();
    context = new ValidationContext();
  }

  @Test
  void validate_shouldDoNothing_whenContentIsNull() {
    contentValidator.validate(null, CONTENT_PATH, context);

    assertTrue(context.errors().isEmpty());
    assertTrue(context.warnings().isEmpty());
  }

  @Test
  void validate_shouldDoNothing_whenContentIsEmpty() {
    contentValidator.validate(List.of(), CONTENT_PATH, context);

    assertTrue(context.errors().isEmpty());
    assertTrue(context.warnings().isEmpty());
  }

  @Test
  void validateContentNode_shouldAddError_whenNodeIsNull() {
    contentValidator.validate(Collections.singletonList(null), CONTENT_PATH, context);

    assertFalse(context.errors().isEmpty());
    assertTrue(context.errors().getFirst().contains("is empty"));
  }

  @Test
  void validateContentNode_shouldAddError_whenTypeIsNull() {
    ContentNode node = new ContentNode(null, VALID_TEXT, null, null);

    contentValidator.validate(List.of(node), CONTENT_PATH, context);

    assertFalse(context.errors().isEmpty());
    assertTrue(context.errors().getFirst().contains("type"));
  }

  @Test
  void validateContentNode_shouldAddError_whenTypeIsUnknown() {
    ContentNode node = new ContentNode(ContentType.UNKNOWN, VALID_TEXT, null, null);

    contentValidator.validate(List.of(node), CONTENT_PATH, context);

    assertFalse(context.errors().isEmpty());
    assertTrue(context.errors().getFirst().contains("type"));
  }

  @Test
  void validateTextContent_shouldAddError_whenTextIsBlank() {
    ContentNode node = new ContentNode(ContentType.TEXT, "   ", null, null);

    contentValidator.validate(List.of(node), CONTENT_PATH, context);

    assertFalse(context.errors().isEmpty());
    assertTrue(context.errors().getFirst().contains("text"));
  }

  @Test
  void validateTextContent_shouldAddError_whenTextIsMissing() {
    ContentNode node = new ContentNode(ContentType.TEXT, null, null, null);

    contentValidator.validate(List.of(node), CONTENT_PATH, context);

    assertFalse(context.errors().isEmpty());
    assertTrue(context.errors().getFirst().contains("text"));
  }

  @Test
  void validateTextContent_shouldAddWarning_whenFileNameIsPresent() {
    ContentNode node = new ContentNode(ContentType.TEXT, VALID_TEXT, VALID_FILE_NAME, null);

    contentValidator.validate(List.of(node), CONTENT_PATH, context);

    assertFalse(context.warnings().isEmpty());
    assertTrue(context.warnings().getFirst().contains("file_name"));
  }

  @Test
  void validateTextContent_shouldAddWarning_whenCaptionIsPresent() {
    ContentNode node = new ContentNode(ContentType.TEXT, VALID_TEXT, null, VALID_CAPTION);

    contentValidator.validate(List.of(node), CONTENT_PATH, context);

    assertFalse(context.warnings().isEmpty());
    assertTrue(context.warnings().getFirst().contains("caption"));
  }

  @Test
  void validateMediaContent_shouldAddError_whenFileNameIsMissing() {
    ContentNode node = new ContentNode(ContentType.AUDIO, null, null, VALID_CAPTION);

    contentValidator.validate(List.of(node), CONTENT_PATH, context);

    assertFalse(context.errors().isEmpty());
    assertTrue(context.errors().getFirst().contains("file_name"));
  }

  @Test
  void validateMediaContent_shouldAddWarning_whenTextIsPresent() {
    ContentNode node = new ContentNode(ContentType.AUDIO, VALID_TEXT, VALID_FILE_NAME, null);

    contentValidator.validate(List.of(node), CONTENT_PATH, context);

    assertFalse(context.warnings().isEmpty());
    assertTrue(context.warnings().getFirst().contains("text"));
  }

  @Test
  void validateMediaFileExists_shouldAddError_whenFileDoesNotExist() {
    ContentNode node = new ContentNode(ContentType.AUDIO, null, INVALID_FILE_NAME, null);

    contentValidator.validate(List.of(node), CONTENT_PATH, context);

    assertFalse(context.errors().isEmpty());
    assertTrue(context.errors().getFirst().contains("missing media file"));
  }

  @Test
  void validateMediaFileExists_shouldNotAddError_whenFileExists() {
    ContentNode node = new ContentNode(ContentType.AUDIO, null, VALID_FILE_NAME, null);

    contentValidator.validate(List.of(node), CONTENT_PATH, context);

    assertTrue(context.errors().isEmpty());
  }
}
