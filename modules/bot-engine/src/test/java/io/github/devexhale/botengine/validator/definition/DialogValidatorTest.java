package io.github.devexhale.botengine.validator.definition;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.devexhale.botengine.domain.dialog.Button;
import io.github.devexhale.botengine.domain.dialog.ButtonType;
import io.github.devexhale.botengine.domain.dialog.DialogMap;
import io.github.devexhale.botengine.domain.dialog.DialogNode;
import io.github.devexhale.botengine.execution.common.command.commandset.StartCommand;
import io.github.devexhale.botengine.testsupport.logger.TestLogCaptor;

import java.util.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DialogValidatorTest {

  private static final String FILE_NAME = "test_dialog.yaml";
  private static final String NODE_KEY = "test_node";
  private static final String MESSAGE_TEXT = "Hello world";
  private static final String BUTTON_LABEL = "Click me";
  private static final String NEXT_NODE_KEY = "next_node";
  private static final String BUTTON_URL = "https://example.com";
  private static final String BUTTONS_FIELD = "buttons";
  private static final String START_NODE_KEY = StartCommand.COMMAND_NAME;
  private static final String INVALID_EMPTY_STRING = "";

  @Mock private ContentValidator contentValidator;
  @InjectMocks private DialogValidator dialogValidator;

  private TestLogCaptor logCaptor;

  @BeforeEach
  void setUp() {
    logCaptor = new TestLogCaptor(AbstractDefinitionValidator.class);
    lenient().doNothing().when(contentValidator).validate(any(), any(), any());
  }

  @AfterEach
  void tearDown() {
    logCaptor.close();
  }

  @Test
  void targetType_shouldReturnDialogMapClass() {
    Class<DialogMap> result = dialogValidator.targetType();

    assertSame(DialogMap.class, result);
  }

  @Test
  void validate_shouldThrowError_whenDialogMapIsNull() {
    Exception exception =
        assertThrows(Exception.class, () -> dialogValidator.validate(null, FILE_NAME));

    assertTrue(exception.getMessage().contains("Dialog map is null"));
  }

  @Test
  void validate_shouldThrowError_whenDialogMapIsEmpty() {
    DialogMap emptyMap = new DialogMap(Map.of());

    Exception exception =
        assertThrows(Exception.class, () -> dialogValidator.validate(emptyMap, FILE_NAME));

    assertTrue(exception.getMessage().contains("Dialog map is empty"));
  }

  @Test
  void validate_shouldThrowError_whenNodeIsNull() {
    Map<String, DialogNode> nodes = new HashMap<>();
    nodes.put(START_NODE_KEY, null);

    DialogMap mapWithNullNode = new DialogMap(nodes);

    Exception exception =
        assertThrows(Exception.class, () -> dialogValidator.validate(mapWithNullNode, FILE_NAME));

    assertTrue(exception.getMessage().contains("is empty"));
  }

  @Test
  void validate_shouldThrowError_whenStartNodeIsMissing() {
    DialogMap mapWithoutStart = new DialogMap(Map.of(NODE_KEY, createValidNode()));

    Exception exception =
        assertThrows(Exception.class, () -> dialogValidator.validate(mapWithoutStart, FILE_NAME));

    assertTrue(exception.getMessage().contains("Dialog map must contain node '/start'"));
  }

  @Test
  void validate_shouldCallContentValidator_forEachNode() {
    DialogNode testNode = createValidNode();
    DialogMap mapWithTestNode =
        new DialogMap(Map.of(START_NODE_KEY, createValidNode(), NODE_KEY, testNode));

    dialogValidator.validate(mapWithTestNode, FILE_NAME);

    verify(contentValidator).validate(eq(testNode.content()), eq(NODE_KEY + ".content"), any());
  }

  @Test
  void validate_shouldThrowError_whenNodeMessageIsMissing() {
    DialogNode nodeWithEmptyMessage =
        new DialogNode(
            List.of(), INVALID_EMPTY_STRING, ButtonType.INLINE, List.of(createValidButton()));

    Exception exception =
        assertThrows(
            Exception.class,
            () ->
                dialogValidator.validate(
                    new DialogMap(Map.of(START_NODE_KEY, nodeWithEmptyMessage)), FILE_NAME));

    assertTrue(exception.getMessage().contains("message"));
  }

  @Test
  void validate_shouldThrowError_whenButtonTypeIsUnknown() {
    DialogNode nodeWithUnknownType =
        new DialogNode(List.of(), MESSAGE_TEXT, ButtonType.UNKNOWN, List.of(createValidButton()));

    Exception exception =
        assertThrows(
            Exception.class,
            () ->
                dialogValidator.validate(
                    new DialogMap(Map.of(START_NODE_KEY, nodeWithUnknownType)), FILE_NAME));

    assertTrue(exception.getMessage().contains("button_type"));
  }

  @Test
  void validate_shouldThrowError_whenButtonsIsNull() {
    DialogNode nodeWithNullButton =
        new DialogNode(List.of(), MESSAGE_TEXT, ButtonType.INLINE, Collections.singletonList(null));

    Exception exception =
        assertThrows(
            Exception.class,
            () ->
                dialogValidator.validate(
                    new DialogMap(Map.of(START_NODE_KEY, nodeWithNullButton)), FILE_NAME));

    assertTrue(exception.getMessage().contains("is empty"));
  }

  @Test
  void validate_shouldThrowError_whenButtonsAreMissing() {
    DialogNode nodeWithNoButtons = new DialogNode(List.of(), MESSAGE_TEXT, ButtonType.INLINE, null);

    Exception exception =
        assertThrows(
            Exception.class,
            () ->
                dialogValidator.validate(
                    new DialogMap(Map.of(START_NODE_KEY, nodeWithNoButtons)), FILE_NAME));

    assertTrue(exception.getMessage().contains(BUTTONS_FIELD));
  }

  @Test
  void validate_shouldThrowError_whenButtonsAreEmpty() {
    DialogNode nodeWithEmptyButtons =
        new DialogNode(List.of(), MESSAGE_TEXT, ButtonType.INLINE, List.of());

    Exception exception =
        assertThrows(
            Exception.class,
            () ->
                dialogValidator.validate(
                    new DialogMap(Map.of(START_NODE_KEY, nodeWithEmptyButtons)), FILE_NAME));

    assertTrue(exception.getMessage().contains(BUTTONS_FIELD));
  }

  @Test
  void validate_shouldThrowError_whenButtonLabelIsMissing() {
    Button invalidButton = new Button(INVALID_EMPTY_STRING, NEXT_NODE_KEY, null);
    DialogNode nodeWithInvalidButton =
        new DialogNode(List.of(), MESSAGE_TEXT, ButtonType.INLINE, List.of(invalidButton));

    Exception exception =
        assertThrows(
            Exception.class,
            () ->
                dialogValidator.validate(
                    new DialogMap(Map.of(START_NODE_KEY, nodeWithInvalidButton)), FILE_NAME));

    assertTrue(exception.getMessage().contains("label"));
  }

  @Test
  void validate_shouldThrowError_whenReplyButtonHasUrl() {
    Button replyWithUrl = new Button(BUTTON_LABEL, NEXT_NODE_KEY, BUTTON_URL);
    DialogNode nodeWithReplyUrl =
        new DialogNode(List.of(), MESSAGE_TEXT, ButtonType.REPLY, List.of(replyWithUrl));

    Exception exception =
        assertThrows(
            Exception.class,
            () ->
                dialogValidator.validate(
                    new DialogMap(Map.of(START_NODE_KEY, nodeWithReplyUrl)), FILE_NAME));

    assertTrue(exception.getMessage().contains("url"));
  }

  @Test
  void validate_shouldThrowError_whenReplyButtonNextIsMissing() {
    Button replyWithoutNext = new Button(BUTTON_LABEL, null, null);
    DialogNode nodeWithReplyNoNext =
        new DialogNode(List.of(), MESSAGE_TEXT, ButtonType.REPLY, List.of(replyWithoutNext));

    Exception exception =
        assertThrows(
            Exception.class,
            () ->
                dialogValidator.validate(
                    new DialogMap(Map.of(START_NODE_KEY, nodeWithReplyNoNext)), FILE_NAME));

    assertTrue(exception.getMessage().contains("next"));
  }

  @Test
  void validate_shouldThrowError_whenInlineButtonHasNeitherUrlNorNext() {
    Button emptyInlineButton = new Button(BUTTON_LABEL, null, null);
    DialogNode nodeWithEmptyInline =
        new DialogNode(List.of(), MESSAGE_TEXT, ButtonType.INLINE, List.of(emptyInlineButton));

    Exception exception =
        assertThrows(
            Exception.class,
            () ->
                dialogValidator.validate(
                    new DialogMap(Map.of(START_NODE_KEY, nodeWithEmptyInline)), FILE_NAME));

    assertTrue(exception.getMessage().contains("must contain either 'next' or 'url'"));
  }

  @Test
  void validate_shouldThrowError_whenInlineButtonHasBothUrlAndNext() {
    Button overloadedInlineButton = new Button(BUTTON_LABEL, NEXT_NODE_KEY, BUTTON_URL);
    DialogNode nodeWithOverloadedInline =
        new DialogNode(List.of(), MESSAGE_TEXT, ButtonType.INLINE, List.of(overloadedInlineButton));

    Exception exception =
        assertThrows(
            Exception.class,
            () ->
                dialogValidator.validate(
                    new DialogMap(Map.of(START_NODE_KEY, nodeWithOverloadedInline)), FILE_NAME));

    assertTrue(exception.getMessage().contains("cannot contain both 'next' and 'url'"));
  }

  @Test
  void validate_shouldThrowError_whenNullTypeButtonHasNeitherUrlNorNext() {
    Button emptyButtonWithNullType = new Button(BUTTON_LABEL, null, null);
    DialogNode nodeWithEmptyButtonAndNullType =
        new DialogNode(List.of(), MESSAGE_TEXT, null, List.of(emptyButtonWithNullType));

    Exception exception =
        assertThrows(
            Exception.class,
            () ->
                dialogValidator.validate(
                    new DialogMap(Map.of(START_NODE_KEY, nodeWithEmptyButtonAndNullType)),
                    FILE_NAME));

    assertTrue(exception.getMessage().contains("must contain either 'next' or 'url'"));
  }

  @Test
  void validate_shouldThrowError_whenNullTypeButtonHasBothUrlAndNext() {
    Button overloadedButtonWithNullType = new Button(BUTTON_LABEL, NEXT_NODE_KEY, BUTTON_URL);
    DialogNode nodeWithOverloadedButtonAndNullType =
        new DialogNode(List.of(), MESSAGE_TEXT, null, List.of(overloadedButtonWithNullType));

    Exception exception =
        assertThrows(
            Exception.class,
            () ->
                dialogValidator.validate(
                    new DialogMap(Map.of(START_NODE_KEY, nodeWithOverloadedButtonAndNullType)),
                    FILE_NAME));

    assertTrue(exception.getMessage().contains("cannot contain both 'next' and 'url'"));
  }

  @Test
  void validate_shouldNotThrow_whenNullTypeButtonContainsOnlyUrl() {
    Button buttonWithNullTypeAndUrl = new Button(BUTTON_LABEL, null, BUTTON_URL);

    DialogNode node =
        new DialogNode(List.of(), MESSAGE_TEXT, null, List.of(buttonWithNullTypeAndUrl));

    assertDoesNotThrow(
        () -> dialogValidator.validate(new DialogMap(Map.of(START_NODE_KEY, node)), FILE_NAME));
  }

  @Test
  void validate_shouldLogWarning_whenDuplicateButtonLabelsExist() {
    String expectedWarning = "duplicate button label";
    Button duplicateButton1 = new Button(BUTTON_LABEL, "next1", null);
    Button duplicateButton2 = new Button(BUTTON_LABEL, "next2", null);
    DialogNode nodeWithDuplicates =
        new DialogNode(
            List.of(),
            MESSAGE_TEXT,
            ButtonType.INLINE,
            List.of(duplicateButton1, duplicateButton2));

    dialogValidator.validate(new DialogMap(Map.of(START_NODE_KEY, nodeWithDuplicates)), FILE_NAME);

    ILoggingEvent loggedEvent = findLogEventContaining(expectedWarning);
    assertEquals(Level.WARN, loggedEvent.getLevel());
    assertTrue(loggedEvent.getFormattedMessage().contains(expectedWarning));
  }

  @Test
  void validate_shouldLogWarning_whenButtonNextPointsToMissingNode() {
    String expectedWarning = "points to missing node";
    Button buttonToMissing = new Button(BUTTON_LABEL, "non_existent_node", null);
    DialogNode nodeWithBrokenLink =
        new DialogNode(List.of(), MESSAGE_TEXT, ButtonType.INLINE, List.of(buttonToMissing));

    dialogValidator.validate(new DialogMap(Map.of(START_NODE_KEY, nodeWithBrokenLink)), FILE_NAME);

    ILoggingEvent loggedEvent = findLogEventContaining(expectedWarning);
    assertEquals(Level.WARN, loggedEvent.getLevel());
    assertTrue(loggedEvent.getFormattedMessage().contains(expectedWarning));
  }

  @Test
  void validate_shouldNotLogWarning_whenButtonNextPointsToExistingNode() {
    Button button = new Button(BUTTON_LABEL, NEXT_NODE_KEY, null);

    DialogNode startNode =
        new DialogNode(List.of(), MESSAGE_TEXT, ButtonType.INLINE, List.of(button));

    DialogNode nextNode = createValidNode();

    DialogMap dialogMap =
        new DialogMap(
            Map.of(
                START_NODE_KEY, startNode,
                NEXT_NODE_KEY, nextNode));

    assertDoesNotThrow(() -> dialogValidator.validate(dialogMap, FILE_NAME));

    assertTrue(
        logCaptor.events().stream()
            .noneMatch(e -> e.getFormattedMessage().contains("points to missing node")));
  }

  @Test
  void validate_shouldNotThrow_whenInlineButtonContainsOnlyUrl() {
    Button inlineButton = new Button(BUTTON_LABEL, null, BUTTON_URL);

    DialogNode node =
        new DialogNode(List.of(), MESSAGE_TEXT, ButtonType.INLINE, List.of(inlineButton));

    assertDoesNotThrow(
        () -> dialogValidator.validate(new DialogMap(Map.of(START_NODE_KEY, node)), FILE_NAME));
  }

  @Test
  void validate_shouldNotThrow_whenAllValidationsPass() {
    DialogMap validMap = new DialogMap(Map.of(START_NODE_KEY, createValidNode()));

    assertDoesNotThrow(() -> dialogValidator.validate(validMap, FILE_NAME));
  }

  private ILoggingEvent findLogEventContaining(String expectedMessagePart) {
    return logCaptor.events().stream()
        .filter(e -> e.getFormattedMessage().contains(expectedMessagePart))
        .findFirst()
        .orElseThrow(
            () ->
                new AssertionError(
                    "Expected log event containing '" + expectedMessagePart + "' not found"));
  }

  private Button createValidButton() {
    return new Button(BUTTON_LABEL, NEXT_NODE_KEY, null);
  }

  private DialogNode createValidNode() {
    return new DialogNode(List.of(), MESSAGE_TEXT, ButtonType.INLINE, List.of(createValidButton()));
  }
}
