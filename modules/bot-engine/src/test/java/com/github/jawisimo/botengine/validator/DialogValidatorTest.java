package com.github.jawisimo.botengine.validator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.github.jawisimo.botengine.exception.DialogLoadingException;
import com.github.jawisimo.botengine.interaction.command.commandset.StartCommand;
import com.github.jawisimo.botengine.interaction.media.handler.MediaHandler;
import java.util.List;
import java.util.Map;

import com.github.jawisimo.botengine.interaction.node.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DialogValidatorTest {

  private static final String NEXT = "next";
  private static final String URL = "https://test.com";
  private static final String SOME_MSG = "Some message...";
  private static final String BUTTON_LABEL = "Button Label";
  private static final String MEDIA_TYPE = "PHOTO";
  private static final String FILE_NAME = "image.jpg";
  private static final String TEST_FILE_NAME = "file.yml";
  private static final String ERROR_TEXT_NULL_OR_EMPTY =
      "Content text is null or empty, but content type is TEXT";
  private static final String ERROR_NO_HANDLER_FOUND = "No handler found for media type: ";
  private static final String ERROR_REPLY_BUTTON_URL = "Reply button cannot have a 'URL'";
  private static final String ERROR_REPLY_BUTTON_NEXT =
      "Reply button must have a 'next' (callback text)";
  private static final String ERROR_INLINE_BUTTON_MISSING =
      "Inline button must have either 'next' or 'URL'";
  private static final String ERROR_INLINE_BUTTON_BOTH =
      "Inline button cannot have both 'next' and 'URL'";

  private DialogValidator validator;

  @Mock private MediaHandler mediaHandler;

  @BeforeEach
  void init() {
    validator = new DialogValidator();
  }

  @Test
  void validateStartNode_shouldNotThrow_whenStartNodeExists() {
    DialogMap dialogMap = new DialogMap(Map.of(StartCommand.COMMAND_NAME, createValidDialogNode()));

    assertDoesNotThrow(() -> validator.validateStartNode(dialogMap, TEST_FILE_NAME));
  }

  @Test
  void validateStartNode_shouldThrowException_whenStartNodeMissing() {
    DialogMap dialogMap = new DialogMap(Map.of());

    DialogLoadingException ex =
        assertThrows(
            DialogLoadingException.class,
            () -> validator.validateStartNode(dialogMap, TEST_FILE_NAME));

    assertTrue(ex.getMessage().contains(StartCommand.COMMAND_NAME));
  }

  @Test
  void validateContent_shouldThrowException_whenTextTypeAndTextIsNull() {
    ContentNode node = new ContentNode(ContentType.TEXT, null, null);
    List<MediaHandler> handlers = List.of();

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> validator.validateContent(node, handlers));

    assertEquals(ERROR_TEXT_NULL_OR_EMPTY, ex.getMessage());
  }

  @Test
  void validateContent_shouldThrowException_whenTextTypeAndTextIsEmpty() {
    ContentNode node = new ContentNode(ContentType.TEXT, "", null);

    List<MediaHandler> handlers = List.of();

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> validator.validateContent(node, handlers));

    assertEquals(ERROR_TEXT_NULL_OR_EMPTY, ex.getMessage());
  }

  @Test
  void validateContent_shouldNotThrow_whenTextTypeAndTextIsValid() {
    ContentNode node = new ContentNode(ContentType.TEXT, SOME_MSG, null);

    assertDoesNotThrow(() -> validator.validateContent(node, List.of()));
  }

  @Test
  void validateContent_shouldNotThrow_whenMediaIsNull() {
    ContentNode node = new ContentNode(ContentType.MEDIA, null, null);
    assertDoesNotThrow(() -> validator.validateContent(node, List.of()));
  }

  @Test
  void validateContent_shouldThrowException_whenNoMediaHandlerSupports() {
    Media media = new Media(MEDIA_TYPE, FILE_NAME, null);
    ContentNode node = new ContentNode(ContentType.MEDIA, null, media);
    String expected = ERROR_NO_HANDLER_FOUND + MEDIA_TYPE + " (file: " + FILE_NAME + ")";

    when(mediaHandler.canHandle(node)).thenReturn(false);

    List<MediaHandler> handlers = List.of(mediaHandler);

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> validator.validateContent(node, handlers));

    assertEquals(expected, ex.getMessage());
  }

  @Test
  void validateContent_shouldNotThrow_whenMediaHandlerSupports() {
    Media media = new Media(MEDIA_TYPE, FILE_NAME, null);
    ContentNode node = new ContentNode(ContentType.MEDIA, null, media);

    when(mediaHandler.canHandle(node)).thenReturn(true);

    assertDoesNotThrow(() -> validator.validateContent(node, List.of(mediaHandler)));
  }

  @Test
  void validateButtons_shouldThrowException_whenReplyButtonHasUrl() {
    Button button = new Button(BUTTON_LABEL, NEXT, URL);
    DialogNode node = createDialogNode(ButtonType.REPLY, button);

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> validator.validateButtons(node));

    assertEquals(ERROR_REPLY_BUTTON_URL, ex.getMessage());
  }

  @Test
  void validateButtons_shouldThrowException_whenReplyButtonHasNoNext() {
    Button button = new Button(BUTTON_LABEL, null, null);
    DialogNode node = createDialogNode(ButtonType.REPLY, button);

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> validator.validateButtons(node));

    assertEquals(ERROR_REPLY_BUTTON_NEXT, ex.getMessage());
  }

  @Test
  void validateButtons_shouldNotThrow_whenReplyButtonIsValid() {
    Button button = new Button(BUTTON_LABEL, NEXT, null);
    DialogNode node = createDialogNode(ButtonType.REPLY, button);

    assertDoesNotThrow(() -> validator.validateButtons(node));
  }

  @Test
  void validateButtons_shouldThrowException_whenInlineButtonHasNeitherUrlNorNext() {
    Button button = new Button(BUTTON_LABEL, null, null);
    DialogNode node = createDialogNode(ButtonType.INLINE, button);

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> validator.validateButtons(node));

    assertEquals(ERROR_INLINE_BUTTON_MISSING, ex.getMessage());
  }

  @Test
  void validateButtons_shouldThrowException_whenInlineButtonHasBothUrlAndNext() {
    Button button = new Button(BUTTON_LABEL, NEXT, URL);
    DialogNode node = createDialogNode(ButtonType.INLINE, button);

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> validator.validateButtons(node));

    assertEquals(ERROR_INLINE_BUTTON_BOTH, ex.getMessage());
  }

  @Test
  void validateButtons_shouldNotThrow_whenInlineButtonHasOnlyUrl() {
    Button button = new Button(BUTTON_LABEL, null, URL);
    DialogNode node = createDialogNode(ButtonType.INLINE, button);

    assertDoesNotThrow(() -> validator.validateButtons(node));
  }

  @Test
  void validateButtons_shouldNotThrow_whenInlineButtonHasOnlyNext() {
    Button button = new Button(BUTTON_LABEL, NEXT, null);
    DialogNode node = createDialogNode(ButtonType.INLINE, button);

    assertDoesNotThrow(() -> validator.validateButtons(node));
  }

  private DialogNode createValidDialogNode() {
    Button button = new Button(BUTTON_LABEL, NEXT, null);
    return createDialogNode(ButtonType.REPLY, button);
  }

  private DialogNode createDialogNode(ButtonType buttonType, Button... buttons) {
    ContentNode content = new ContentNode(ContentType.TEXT, SOME_MSG, null);
    return new DialogNode(List.of(content), "Test message", buttonType, List.of(buttons));
  }
}
