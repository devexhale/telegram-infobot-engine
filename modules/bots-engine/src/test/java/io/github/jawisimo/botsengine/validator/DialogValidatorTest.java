package io.github.jawisimo.botsengine.validator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.jawisimo.botsengine.exception.DialogLoadingException;
import io.github.jawisimo.botsengine.interaction.content.handler.ContentHandler;
import java.util.*;

import io.github.jawisimo.botsengine.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
class DialogValidatorTest {

  private static final String DIALOG_FILE = "dialog.yml";
  private static final String START_KEY = "/start";
  private static final String VALID_URL = "https://test.com";

  @Mock private ContentHandler contentHandler;
  private DialogValidator validator;

  @BeforeEach
  void init() {
    validator = new DialogValidator(List.of(contentHandler));
  }

  @Test
  void validate_shouldPass_whenDialogIsPerfectlyValid() {
    DialogNode start =
        new DialogNode(null, "Hello", ButtonType.INLINE, List.of(new Button("Go", "next", null)));
    DialogNode next =
        new DialogNode(null, "End", ButtonType.REPLY, List.of(new Button("Back", START_KEY, null)));

    DialogMap map = new DialogMap(Map.of(START_KEY, start, "next", next));

    assertDoesNotThrow(() -> validator.validate(map, DIALOG_FILE));
  }

  // dialog map

  @Test
  void validate_shouldThrow_whenDialogMapIsNull() {
    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> validator.validate(null, DIALOG_FILE));

    assertTrue(ex.getMessage().contains("Dialog map is null in file 'dialog.yml'"));
  }

  @Test
  void validate_shouldThrow_whenDialogMapIsEmpty() {
    DialogMap map = new DialogMap(Collections.emptyMap());

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> validator.validate(map, DIALOG_FILE));

    assertTrue(ex.getMessage().contains("Dialog map is empty in file 'dialog.yml'"));
  }

  @Test
  void validate_shouldThrow_whenStartNodeIsMissing() {
    DialogMap map =
        new DialogMap(
            Map.of(
                "wrong_node",
                new DialogNode(
                    null, "Msg", ButtonType.INLINE, List.of(new Button("L", "n", null)))));

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> validator.validate(map, DIALOG_FILE));

    assertTrue(ex.getMessage().contains("must contain node '/start'"));
  }

  // nodes

  @Test
  void validate_shouldThrow_whenNodeIsNull() {
    Map<String, DialogNode> nodes = new LinkedHashMap<>();
    nodes.put(START_KEY, null);
    DialogMap map = new DialogMap(nodes);

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> validator.validate(map, DIALOG_FILE));

    assertTrue(ex.getMessage().contains("Node '/start' is empty"));
  }

  @Test
  void validate_shouldThrow_whenButtonTypeIsUnknown() {
    DialogNode node =
        new DialogNode(null, "Msg", ButtonType.UNKNOWN, List.of(new Button("Label", "next", null)));
    DialogMap map = new DialogMap(Map.of(START_KEY, node));

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> validator.validate(map, DIALOG_FILE));

    assertTrue(ex.getMessage().contains("Node '/start.button_type' is not valid"));
  }

  // content

  @ParameterizedTest
  @ValueSource(strings = {"NULL", "UNKNOWN"})
  void validate_shouldThrow_whenContentTypeIsMissingOrUnsupported(String typeCase) {
    ContentType type =
        switch (typeCase) {
          case "NULL" -> null;
          case "UNKNOWN" -> ContentType.UNKNOWN;
          default -> throw new IllegalArgumentException("Unexpected type case: " + typeCase);
        };

    ContentNode contentNode = new ContentNode(type, "text", null);
    DialogNode node =
        new DialogNode(
            List.of(contentNode),
            "Msg",
            ButtonType.INLINE,
            List.of(new Button("Label", null, VALID_URL)));
    DialogMap map = new DialogMap(Map.of(START_KEY, node));

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> validator.validate(map, DIALOG_FILE));

    assertTrue(
        ex.getMessage().contains("Node '/start.content[0].type' is missing or not supported"));
  }

  @Test
  void validate_shouldReportCorrectPath_whenContentNodeIsNull() {
    List<ContentNode> content = new ArrayList<>();
    content.add(new ContentNode(ContentType.TEXT, "Valid", null));
    content.add(null);

    DialogNode node =
        new DialogNode(content, "Msg", ButtonType.INLINE, List.of(new Button("L", "n", null)));
    DialogMap map = new DialogMap(Map.of(START_KEY, node));

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> validator.validate(map, DIALOG_FILE));

    assertTrue(ex.getMessage().contains("Node '/start.content[1]' is empty"));
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"   "})
  void validate_shouldThrow_whenTextContentHasNoText(String invalidText) {
    ContentNode contentNode = new ContentNode(ContentType.TEXT, invalidText, null);
    DialogNode node =
        new DialogNode(
            List.of(contentNode),
            "Msg",
            ButtonType.INLINE,
            List.of(new Button("Label", null, VALID_URL)));
    DialogMap map = new DialogMap(Map.of(START_KEY, node));

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> validator.validate(map, DIALOG_FILE));

    assertTrue(
        ex.getMessage()
            .contains("Node '/start.content[0].text' is missing or blank for text content"));
  }

  @Test
  void validate_shouldThrow_whenTextContentContainsMedia() {
    ContentNode contentNode =
        new ContentNode(ContentType.TEXT, "Valid text", new Media("photo", "History.jpg", null));
    DialogNode node =
        new DialogNode(
            List.of(contentNode),
            "Msg",
            ButtonType.INLINE,
            List.of(new Button("Label", null, VALID_URL)));
    DialogMap map = new DialogMap(Map.of(START_KEY, node));

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> validator.validate(map, DIALOG_FILE));

    assertTrue(
        ex.getMessage()
            .contains("Node '/start.content[0].media' must not be present for text type"));
  }

  @Test
  void validate_shouldThrow_whenMediaContentHasNoMedia() {
    ContentNode contentNode = new ContentNode(ContentType.MEDIA, null, null);
    DialogNode node =
        new DialogNode(
            List.of(contentNode),
            "Msg",
            ButtonType.INLINE,
            List.of(new Button("Label", null, VALID_URL)));
    DialogMap map = new DialogMap(Map.of(START_KEY, node));

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> validator.validate(map, DIALOG_FILE));

    assertTrue(
        ex.getMessage().contains("Node '/start.content[0].media' is missing for media content"));
  }

  @Test
  void validate_shouldThrow_whenMediaContentContainsText() {
    ContentNode contentNode =
        new ContentNode(ContentType.MEDIA, "unexpected", new Media("photo", "History.jpg", null));
    DialogNode node =
        new DialogNode(
            List.of(contentNode),
            "Msg",
            ButtonType.INLINE,
            List.of(new Button("Label", null, VALID_URL)));
    DialogMap map = new DialogMap(Map.of(START_KEY, node));

    when(contentHandler.canHandle(any())).thenReturn(true);

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> validator.validate(map, DIALOG_FILE));

    assertTrue(
        ex.getMessage()
            .contains("Node '/start.content[0].text' must not be present for media type"));
  }

  // media

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"   "})
  void validate_shouldThrow_whenMediaTypeIsInvalid(String invalidType) {
    ContentNode contentNode =
        new ContentNode(ContentType.MEDIA, null, new Media(invalidType, "History.jpg", null));
    DialogNode node =
        new DialogNode(
            List.of(contentNode),
            "Msg",
            ButtonType.INLINE,
            List.of(new Button("Label", null, VALID_URL)));
    DialogMap map = new DialogMap(Map.of(START_KEY, node));

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> validator.validate(map, DIALOG_FILE));

    assertTrue(ex.getMessage().contains("Node '/start.content[0].media.type' is missing or blank"));
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"   "})
  void validate_shouldThrow_whenMediaFileNameIsInvalid(String invalidFileName) {
    ContentNode contentNode =
        new ContentNode(ContentType.MEDIA, null, new Media("photo", invalidFileName, null));
    DialogNode node =
        new DialogNode(
            List.of(contentNode),
            "Msg",
            ButtonType.INLINE,
            List.of(new Button("Label", null, VALID_URL)));
    DialogMap map = new DialogMap(Map.of(START_KEY, node));

    when(contentHandler.canHandle(any())).thenReturn(true);

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> validator.validate(map, DIALOG_FILE));

    assertTrue(
        ex.getMessage().contains("Node '/start.content[0].media.file_name' is missing or blank"));
  }

  @Test
  void validate_shouldThrow_whenMediaFileDoesNotExist() {
    ContentNode contentNode =
        new ContentNode(ContentType.MEDIA, null, new Media("photo", "missing-file.jpg", null));
    DialogNode node =
        new DialogNode(
            List.of(contentNode),
            "Msg",
            ButtonType.INLINE,
            List.of(new Button("Label", null, VALID_URL)));
    DialogMap map = new DialogMap(Map.of(START_KEY, node));

    when(contentHandler.canHandle(any())).thenReturn(true);

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> validator.validate(map, DIALOG_FILE));

    assertTrue(
        ex.getMessage()
            .contains(
                "Node '/start.content[0].media.file_name' points to missing media file 'media/missing-file.jpg'"));
  }

  @Test
  void validate_shouldThrow_whenMediaHandlerNotFound() {
    ContentNode mediaNode =
        new ContentNode(ContentType.MEDIA, null, new Media("incorrectType", "test.mp4", null));
    DialogNode node =
        new DialogNode(
            List.of(mediaNode),
            "Msg",
            ButtonType.INLINE,
            List.of(new Button("Label", null, VALID_URL)));
    DialogMap map = new DialogMap(Map.of(START_KEY, node));

    when(contentHandler.canHandle(any())).thenReturn(false);

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> validator.validate(map, DIALOG_FILE));

    assertTrue(ex.getMessage().contains("is not supported"));
  }

  // message

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"   "})
  void validate_shouldThrow_whenMessageIsInvalid(String invalidMsg) {
    DialogNode node =
        new DialogNode(
            null, invalidMsg, ButtonType.INLINE, List.of(new Button("Label", null, VALID_URL)));
    DialogMap map = new DialogMap(Map.of(START_KEY, node));

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> validator.validate(map, DIALOG_FILE));

    assertTrue(ex.getMessage().contains("message' is missing or blank"));
  }

  // keyboard

  @Test
  void validate_shouldThrow_whenButtonListIsEmpty() {
    DialogNode node = new DialogNode(null, "Msg", ButtonType.INLINE, List.of());
    DialogMap map = new DialogMap(Map.of(START_KEY, node));

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> validator.validate(map, DIALOG_FILE));

    assertTrue(ex.getMessage().contains("buttons' is missing or empty"));
  }

  @Test
  void validate_shouldReportCorrectIndex_whenButtonIsNull() {
    List<Button> buttons = new ArrayList<>();
    buttons.add(new Button("Valid", "next", null));
    buttons.add(null);

    DialogNode node = new DialogNode(null, "Msg", ButtonType.INLINE, buttons);
    DialogMap map = new DialogMap(Map.of(START_KEY, node));

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> validator.validate(map, DIALOG_FILE));

    assertTrue(ex.getMessage().contains("Node '/start.buttons[1]' is empty"));
  }

  @Test
  void validate_shouldReportMultipleErrorsInOrder() {
    Button badButton = new Button(" ", null, null);
    DialogNode node = new DialogNode(null, "  ", ButtonType.INLINE, List.of(badButton));
    DialogMap map = new DialogMap(Map.of(START_KEY, node));

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> validator.validate(map, DIALOG_FILE));

    String msg = ex.getMessage();
    assertTrue(msg.contains("/start.message"));
    assertTrue(msg.contains("/start.buttons[0].label"));
    assertTrue(msg.indexOf("/start.message") < msg.indexOf("/start.buttons[0].label"));
  }

  @Test
  void validate_shouldThrow_whenInlineButtonHasBothNextAndUrl() {
    Button badButton = new Button("Label", "next_node", "https://url.com");
    DialogNode node = new DialogNode(null, "Msg", ButtonType.INLINE, List.of(badButton));
    DialogMap map = new DialogMap(Map.of(START_KEY, node));

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> validator.validate(map, DIALOG_FILE));

    assertTrue(ex.getMessage().contains("cannot contain both 'next' and 'url'"));
  }

  @Test
  void validate_shouldThrow_whenInlineButtonHasNeitherNextNorUrl() {
    Button emptyInline = new Button("Label", null, null);
    DialogNode node = new DialogNode(null, "Msg", ButtonType.INLINE, List.of(emptyInline));
    DialogMap map = new DialogMap(Map.of(START_KEY, node));

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> validator.validate(map, DIALOG_FILE));

    assertTrue(ex.getMessage().contains("must contain either 'next' or 'url'"));
  }

  @ParameterizedTest
  @CsvSource({
    "next, https://google.com, must not be present for reply button",
    ", , is missing or blank for reply button"
  })
  void validate_shouldThrow_whenReplyButtonIsInvalid(
      String next, String url, String expectedMessagePart) {
    Button replyButton = new Button("Label", next, url);
    DialogNode node = new DialogNode(null, "Msg", ButtonType.REPLY, List.of(replyButton));
    DialogMap map = new DialogMap(Map.of(START_KEY, node));

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> validator.validate(map, DIALOG_FILE));

    assertTrue(ex.getMessage().contains(expectedMessagePart));
  }

  @Test
  void validate_shouldLogWarning_whenNextNodeIsMissing() {
    DialogNode node =
        new DialogNode(
            null, "Msg", ButtonType.INLINE, List.of(new Button("Go", "ghost_node", null)));
    DialogMap map = new DialogMap(Map.of(START_KEY, node));

    Logger logger = (Logger) LoggerFactory.getLogger(DialogValidator.class);
    ListAppender<ILoggingEvent> appender = new ListAppender<>();
    appender.start();
    logger.addAppender(appender);

    validator.validate(map, DIALOG_FILE);

    assertTrue(appender.list.stream().anyMatch(e -> e.getLevel() == Level.WARN));
    assertTrue(
        appender
            .list
            .getFirst()
            .getFormattedMessage()
            .contains("points to missing node 'ghost_node'"));

    logger.detachAppender(appender);
  }
}
