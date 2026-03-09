package com.github.jawisimo.botengine.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.github.jawisimo.botengine.exception.DialogLoadingException;
import com.github.jawisimo.botengine.interaction.content.handler.ContentHandler;
import com.github.jawisimo.botengine.model.Button;
import com.github.jawisimo.botengine.model.ButtonType;
import com.github.jawisimo.botengine.model.ContentNode;
import com.github.jawisimo.botengine.model.ContentType;
import com.github.jawisimo.botengine.model.DialogMap;
import com.github.jawisimo.botengine.model.DialogNode;
import com.github.jawisimo.botengine.model.Media;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
class DialogValidatorTest {

  private static final String DIALOG_FILE_NAME = "dialog.yml";
  private static final String START_NODE_KEY = "/start";
  private static final String NEXT_NODE_KEY = "history_q1";
  private static final String MISSING_NODE_KEY = "missing_node";
  private static final String MESSAGE = "Start message";
  private static final String BUTTON_LABEL = "Next";
  private static final String URL = "https://example.com";
  private static final String MISSING_MEDIA_FILE = "missing-file.jpg";

  @Mock private ContentHandler contentHandler;

  private DialogValidator dialogValidator;

  @BeforeEach
  void init() {
    dialogValidator = new DialogValidator(List.of(contentHandler));
  }

  @Test
  void validate_shouldThrowException_whenDialogMapIsNull() {
    DialogLoadingException exception =
        assertThrows(
            DialogLoadingException.class, () -> dialogValidator.validate(null, DIALOG_FILE_NAME));

    assertTrue(exception.getMessage().contains("Dialog map is null in file 'dialog.yml'"));
    assertTrue(
        exception
            .getMessage()
            .contains("Dialog loading failed with 1 errors for file 'dialog.yml':"));
  }

  @Test
  void validate_shouldThrowException_whenDialogMapIsEmpty() {
    DialogMap dialogMap = new DialogMap(new LinkedHashMap<>());

    DialogLoadingException exception =
        assertThrows(
            DialogLoadingException.class,
            () -> dialogValidator.validate(dialogMap, DIALOG_FILE_NAME));

    assertTrue(exception.getMessage().contains("Dialog map is empty in file 'dialog.yml'"));
    assertTrue(exception.getMessage().contains("Dialog must contain node '/start' in file"));
    assertTrue(
        exception
            .getMessage()
            .contains("Dialog loading failed with 2 errors for file 'dialog.yml':"));
  }

  @Test
  void validate_shouldThrowException_whenStartNodeIsMissing() {
    DialogNode dialogNode = validInlineNodeWithUrlButton();
    DialogMap dialogMap = dialogMap(Map.of("history_q1", dialogNode));

    DialogLoadingException exception =
        assertThrows(
            DialogLoadingException.class,
            () -> dialogValidator.validate(dialogMap, DIALOG_FILE_NAME));

    assertTrue(exception.getMessage().contains("Dialog must contain node '/start' in file"));
  }

  @Test
  void validate_shouldThrowException_whenMessageIsBlank() {
    DialogNode dialogNode =
        new DialogNode(
            null, "   ", ButtonType.INLINE, List.of(new Button(BUTTON_LABEL, null, URL)));
    DialogMap dialogMap = dialogMap(Map.of(START_NODE_KEY, dialogNode));

    DialogLoadingException exception =
        assertThrows(
            DialogLoadingException.class,
            () -> dialogValidator.validate(dialogMap, DIALOG_FILE_NAME));

    assertTrue(exception.getMessage().contains("Node '/start.message' is missing or blank"));
  }

  @Test
  void validate_shouldThrowException_whenButtonTypeIsUnknown() {
    DialogNode dialogNode =
        new DialogNode(
            null,
            MESSAGE,
            ButtonType.UNKNOWN,
            List.of(new Button(BUTTON_LABEL, NEXT_NODE_KEY, null)));
    DialogMap dialogMap = dialogMap(orderedNodes(dialogNode, validReplyNode()));

    DialogLoadingException exception =
        assertThrows(
            DialogLoadingException.class,
            () -> dialogValidator.validate(dialogMap, DIALOG_FILE_NAME));

    assertTrue(exception.getMessage().contains("Node '/start.button_type' is not valid"));
  }

  @Test
  void validate_shouldThrowException_whenButtonsAreMissing() {
    DialogNode dialogNode = new DialogNode(null, MESSAGE, ButtonType.INLINE, null);
    DialogMap dialogMap = dialogMap(Map.of(START_NODE_KEY, dialogNode));

    DialogLoadingException exception =
        assertThrows(
            DialogLoadingException.class,
            () -> dialogValidator.validate(dialogMap, DIALOG_FILE_NAME));

    assertTrue(exception.getMessage().contains("Node '/start.buttons' is missing or empty"));
  }

  @Test
  void validate_shouldThrowException_whenReplyButtonContainsUrl() {
    Button button = new Button(BUTTON_LABEL, null, URL);
    DialogNode dialogNode = new DialogNode(null, MESSAGE, ButtonType.REPLY, List.of(button));
    DialogMap dialogMap = dialogMap(Map.of(START_NODE_KEY, dialogNode));

    DialogLoadingException exception =
        assertThrows(
            DialogLoadingException.class,
            () -> dialogValidator.validate(dialogMap, DIALOG_FILE_NAME));

    assertTrue(
        exception
            .getMessage()
            .contains("Node '/start.buttons[0].url' must not be present for reply button"));
    assertTrue(
        exception
            .getMessage()
            .contains("Node '/start.buttons[0].next' is missing or blank for reply button"));
  }

  @Test
  void validate_shouldThrowException_whenInlineButtonContainsNextAndUrl() {
    Button button = new Button(BUTTON_LABEL, NEXT_NODE_KEY, URL);
    DialogNode dialogNode = new DialogNode(null, MESSAGE, ButtonType.INLINE, List.of(button));
    DialogMap dialogMap = dialogMap(orderedNodes(dialogNode, validReplyNode()));

    DialogLoadingException exception =
        assertThrows(
            DialogLoadingException.class,
            () -> dialogValidator.validate(dialogMap, DIALOG_FILE_NAME));

    assertTrue(
        exception
            .getMessage()
            .contains("Node '/start.buttons[0]' cannot contain both 'next' and 'url'"));
  }

  @Test
  void validate_shouldThrowException_whenTextContentTextIsBlank() {
    ContentNode contentNode = new ContentNode(ContentType.TEXT, " ", null);
    DialogNode dialogNode =
        new DialogNode(
            List.of(contentNode),
            MESSAGE,
            ButtonType.INLINE,
            List.of(new Button(BUTTON_LABEL, null, URL)));
    DialogMap dialogMap = dialogMap(Map.of(START_NODE_KEY, dialogNode));

    DialogLoadingException exception =
        assertThrows(
            DialogLoadingException.class,
            () -> dialogValidator.validate(dialogMap, DIALOG_FILE_NAME));

    assertTrue(
        exception
            .getMessage()
            .contains("Node '/start.content[0].text' is missing or blank for text content"));
  }

  @Test
  void validate_shouldThrowException_whenMediaContentIsMissing() {
    ContentNode contentNode = new ContentNode(ContentType.MEDIA, null, null);
    DialogNode dialogNode =
        new DialogNode(
            List.of(contentNode),
            MESSAGE,
            ButtonType.INLINE,
            List.of(new Button(BUTTON_LABEL, null, URL)));
    DialogMap dialogMap = dialogMap(Map.of(START_NODE_KEY, dialogNode));

    DialogLoadingException exception =
        assertThrows(
            DialogLoadingException.class,
            () -> dialogValidator.validate(dialogMap, DIALOG_FILE_NAME));

    assertTrue(
        exception
            .getMessage()
            .contains("Node '/start.content[0].media' is missing for media content"));
  }

  @Test
  void validate_shouldThrowException_whenMediaFileDoesNotExist() {
    ContentNode contentNode =
        new ContentNode(ContentType.MEDIA, null, new Media("photo", MISSING_MEDIA_FILE, null));
    DialogNode dialogNode =
        new DialogNode(
            List.of(contentNode),
            MESSAGE,
            ButtonType.INLINE,
            List.of(new Button(BUTTON_LABEL, null, URL)));
    DialogMap dialogMap = dialogMap(Map.of(START_NODE_KEY, dialogNode));

    when(contentHandler.canHandle(any(ContentNode.class))).thenReturn(true);

    DialogLoadingException exception =
        assertThrows(
            DialogLoadingException.class,
            () -> dialogValidator.validate(dialogMap, DIALOG_FILE_NAME));

    assertTrue(
        exception
            .getMessage()
            .contains(
                "Node '/start.content[0].media.file_name' points to missing media file 'media/missing-file.jpg'"));
  }

  @Test
  void validate_shouldLogErrorAndNotThrow_whenNextNodeIsMissing() {
    DialogNode dialogNode =
        new DialogNode(
            null,
            MESSAGE,
            ButtonType.INLINE,
            List.of(new Button(BUTTON_LABEL, MISSING_NODE_KEY, null)));
    DialogMap dialogMap = dialogMap(Map.of(START_NODE_KEY, dialogNode));
    Logger logger = (Logger) LoggerFactory.getLogger(DialogValidator.class);
    ListAppender<ILoggingEvent> appender = new ListAppender<>();

    appender.start();
    logger.addAppender(appender);

    assertDoesNotThrow(() -> dialogValidator.validate(dialogMap, DIALOG_FILE_NAME));

    logger.detachAppender(appender);

    assertTrue(appender.list.stream().anyMatch(event -> event.getLevel() == Level.ERROR));
    assertTrue(
        appender.list.stream()
            .anyMatch(
                event ->
                    event
                        .getFormattedMessage()
                        .contains(
                            "Node '/start.buttons[0].next' points to missing node 'missing_node'")));
  }

  @Test
  void
      validate_shouldThrowSingleExceptionWithAllErrors_whenDialogContainsMultipleStructuralIssues() {
    ContentNode contentNode = new ContentNode(ContentType.TEXT, " ", null);
    DialogNode dialogNode =
        new DialogNode(
            List.of(contentNode), " ", ButtonType.REPLY, List.of(new Button(" ", null, URL)));
    DialogMap dialogMap = dialogMap(Map.of(START_NODE_KEY, dialogNode));

    DialogLoadingException exception =
        assertThrows(
            DialogLoadingException.class,
            () -> dialogValidator.validate(dialogMap, DIALOG_FILE_NAME));

    assertTrue(
        exception
            .getMessage()
            .contains("Dialog loading failed with 5 errors for file 'dialog.yml':"));
    assertTrue(
        exception
            .getMessage()
            .contains("Node '/start.content[0].text' is missing or blank for text content"));
    assertTrue(exception.getMessage().contains("Node '/start.message' is missing or blank"));
    assertTrue(
        exception.getMessage().contains("Node '/start.buttons[0].label' is missing or blank"));
    assertTrue(
        exception
            .getMessage()
            .contains("Node '/start.buttons[0].url' must not be present for reply button"));
    assertTrue(
        exception
            .getMessage()
            .contains("Node '/start.buttons[0].next' is missing or blank for reply button"));
  }

  private DialogMap dialogMap(Map<String, DialogNode> nodes) {
    return new DialogMap(new LinkedHashMap<>(nodes));
  }

  private Map<String, DialogNode> orderedNodes(DialogNode firstNode, DialogNode secondNode) {
    Map<String, DialogNode> nodes = new LinkedHashMap<>();
    nodes.put(START_NODE_KEY, firstNode);
    nodes.put(NEXT_NODE_KEY, secondNode);

    return nodes;
  }

  private DialogNode validInlineNodeWithUrlButton() {
    return new DialogNode(
        null, MESSAGE, ButtonType.INLINE, List.of(new Button(BUTTON_LABEL, null, URL)));
  }

  private DialogNode validReplyNode() {
    return new DialogNode(
        null, MESSAGE, ButtonType.REPLY, List.of(new Button(BUTTON_LABEL, START_NODE_KEY, null)));
  }
}
