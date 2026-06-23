package io.github.devexhale.botengine.validator.definition;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.devexhale.botengine.domain.broadcast.BroadcastMap;
import io.github.devexhale.botengine.domain.broadcast.BroadcastNode;
import io.github.devexhale.botengine.testsupport.logger.TestLogCaptor;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BroadcastValidatorTest {

  private static final String FILE_NAME = "test_broadcast.yaml";
  private static final String NODE_KEY = "test_node";
  private static final String MESSAGE_TEXT = "Broadcast message";
  private static final String RETURN_BUTTON_LABEL = "Go Back";
  private static final LocalDateTime VALID_START_AT = LocalDateTime.now().plusDays(1);
  private static final Integer VALID_TOTAL_SENDS = 3;
  private static final Duration VALID_INTERVAL = Duration.ofHours(1);
  private static final Integer INVALID_ZERO_SENDS = 0;
  private static final Integer INVALID_NEGATIVE_SENDS = -1;
  private static final Duration INVALID_ZERO_INTERVAL = Duration.ZERO;
  private static final Duration INVALID_NEGATIVE_INTERVAL = Duration.ofHours(-1);
  private static final String INVALID_EMPTY_STRING = "";

  @Mock private ContentValidator contentValidator;
  @InjectMocks private BroadcastValidator broadcastValidator;

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
  void targetType_shouldReturnBroadcastMapClass() {
    Class<BroadcastMap> result = broadcastValidator.targetType();

    assertSame(BroadcastMap.class, result);
  }

  @Test
  void validate_shouldThrowError_whenBroadcastMapIsNull() {
    Exception exception =
        assertThrows(Exception.class, () -> broadcastValidator.validate(null, FILE_NAME));

    assertTrue(exception.getMessage().contains("Broadcast map is null"));
  }

  @Test
  void validate_shouldThrowError_whenBroadcastMapIsEmpty() {
    BroadcastMap emptyMap = new BroadcastMap(Map.of());

    Exception exception =
        assertThrows(Exception.class, () -> broadcastValidator.validate(emptyMap, FILE_NAME));

    assertTrue(exception.getMessage().contains("Broadcast map is empty"));
  }

  @Test
  void validate_shouldThrowError_whenNodeIsNull() {
    Map<String, BroadcastNode> nodes = new java.util.HashMap<>();
    nodes.put(NODE_KEY, null);

    BroadcastMap broadcastMap = new BroadcastMap(nodes);

    Exception exception =
        assertThrows(Exception.class, () -> broadcastValidator.validate(broadcastMap, FILE_NAME));

    assertTrue(exception.getMessage().contains("is empty"));
  }

  @Test
  void validate_shouldCallContentValidator_forEachNode() {
    BroadcastNode testNode = createValidNode();
    BroadcastMap mapWithTestNode = new BroadcastMap(Map.of(NODE_KEY, testNode));

    broadcastValidator.validate(mapWithTestNode, FILE_NAME);

    verify(contentValidator).validate(eq(testNode.content()), eq(NODE_KEY + ".content"), any());
  }

  @Test
  void validate_shouldThrowError_whenNodeMessageIsMissing() {
    BroadcastNode nodeWithEmptyMessage =
        new BroadcastNode(
            List.of(),
            INVALID_EMPTY_STRING,
            RETURN_BUTTON_LABEL,
            VALID_START_AT,
            VALID_TOTAL_SENDS,
            VALID_INTERVAL);

    Exception exception =
        assertThrows(
            Exception.class,
            () ->
                broadcastValidator.validate(
                    new BroadcastMap(Map.of(NODE_KEY, nodeWithEmptyMessage)), FILE_NAME));

    assertTrue(exception.getMessage().contains("message"));
  }

  @Test
  void validate_shouldThrowError_whenReturnButtonLabelIsMissing() {
    BroadcastNode nodeWithEmptyLabel =
        new BroadcastNode(
            List.of(),
            MESSAGE_TEXT,
            INVALID_EMPTY_STRING,
            VALID_START_AT,
            VALID_TOTAL_SENDS,
            VALID_INTERVAL);

    Exception exception =
        assertThrows(
            Exception.class,
            () ->
                broadcastValidator.validate(
                    new BroadcastMap(Map.of(NODE_KEY, nodeWithEmptyLabel)), FILE_NAME));

    assertTrue(exception.getMessage().contains("return_label"));
  }

  @Test
  void validate_shouldThrowError_whenStartAtIsNull() {
    BroadcastNode nodeWithNullStart =
        new BroadcastNode(
            List.of(), MESSAGE_TEXT, RETURN_BUTTON_LABEL, null, VALID_TOTAL_SENDS, VALID_INTERVAL);

    Exception exception =
        assertThrows(
            Exception.class,
            () ->
                broadcastValidator.validate(
                    new BroadcastMap(Map.of(NODE_KEY, nodeWithNullStart)), FILE_NAME));

    assertTrue(exception.getMessage().contains("start_at"));
  }

  @Test
  void validate_shouldThrowError_whenTotalSendsIsZero() {
    BroadcastNode nodeWithZeroSends =
        new BroadcastNode(
            List.of(),
            MESSAGE_TEXT,
            RETURN_BUTTON_LABEL,
            VALID_START_AT,
            INVALID_ZERO_SENDS,
            VALID_INTERVAL);

    Exception exception =
        assertThrows(
            Exception.class,
            () ->
                broadcastValidator.validate(
                    new BroadcastMap(Map.of(NODE_KEY, nodeWithZeroSends)), FILE_NAME));

    assertTrue(exception.getMessage().contains("must be greater than 0"));
  }

  @Test
  void validate_shouldThrowError_whenTotalSendsIsNegative() {
    BroadcastNode nodeWithNegativeSends =
        new BroadcastNode(
            List.of(),
            MESSAGE_TEXT,
            RETURN_BUTTON_LABEL,
            VALID_START_AT,
            INVALID_NEGATIVE_SENDS,
            VALID_INTERVAL);

    Exception exception =
        assertThrows(
            Exception.class,
            () ->
                broadcastValidator.validate(
                    new BroadcastMap(Map.of(NODE_KEY, nodeWithNegativeSends)), FILE_NAME));

    assertTrue(exception.getMessage().contains("must be greater than 0"));
  }

  @Test
  void validate_shouldNotThrow_whenTotalSendsIsNull() {
    BroadcastNode node =
        new BroadcastNode(
            List.of(), MESSAGE_TEXT, RETURN_BUTTON_LABEL, VALID_START_AT, null, VALID_INTERVAL);

    BroadcastMap map = new BroadcastMap(Map.of(NODE_KEY, node));

    assertDoesNotThrow(() -> broadcastValidator.validate(map, FILE_NAME));
  }

  @Test
  void validate_shouldThrowError_whenIntervalIsNullButTotalSendsIsCustom() {
    BroadcastNode nodeWithCustomSendsNoInterval =
        new BroadcastNode(
            List.of(), MESSAGE_TEXT, RETURN_BUTTON_LABEL, VALID_START_AT, VALID_TOTAL_SENDS, null);

    Exception exception =
        assertThrows(
            Exception.class,
            () ->
                broadcastValidator.validate(
                    new BroadcastMap(Map.of(NODE_KEY, nodeWithCustomSendsNoInterval)), FILE_NAME));

    assertTrue(exception.getMessage().contains("interval"));
  }

  @Test
  void validate_shouldNotThrow_whenIntervalIsNullAndTotalSendsIsDefault() {
    BroadcastNode node =
        new BroadcastNode(List.of(), MESSAGE_TEXT, RETURN_BUTTON_LABEL, VALID_START_AT, 1, null);

    BroadcastMap map = new BroadcastMap(Map.of(NODE_KEY, node));

    assertDoesNotThrow(() -> broadcastValidator.validate(map, FILE_NAME));
  }

  @Test
  void validate_shouldThrowError_whenIntervalIsZero() {
    BroadcastNode nodeWithZeroInterval =
        new BroadcastNode(
            List.of(),
            MESSAGE_TEXT,
            RETURN_BUTTON_LABEL,
            VALID_START_AT,
            VALID_TOTAL_SENDS,
            INVALID_ZERO_INTERVAL);

    Exception exception =
        assertThrows(
            Exception.class,
            () ->
                broadcastValidator.validate(
                    new BroadcastMap(Map.of(NODE_KEY, nodeWithZeroInterval)), FILE_NAME));

    assertTrue(exception.getMessage().contains("must be greater than 0"));
  }

  @Test
  void validate_shouldThrowError_whenIntervalIsNegative() {
    BroadcastNode nodeWithNegativeInterval =
        new BroadcastNode(
            List.of(),
            MESSAGE_TEXT,
            RETURN_BUTTON_LABEL,
            VALID_START_AT,
            VALID_TOTAL_SENDS,
            INVALID_NEGATIVE_INTERVAL);

    Exception exception =
        assertThrows(
            Exception.class,
            () ->
                broadcastValidator.validate(
                    new BroadcastMap(Map.of(NODE_KEY, nodeWithNegativeInterval)), FILE_NAME));

    assertTrue(exception.getMessage().contains("must be greater than 0"));
  }

  @Test
  void validate_shouldNotThrow_whenAllValidationsPass() {
    BroadcastMap validMap = new BroadcastMap(Map.of(NODE_KEY, createValidNode()));

    assertDoesNotThrow(() -> broadcastValidator.validate(validMap, FILE_NAME));
  }

  @Test
  void validate_shouldLogWarning_whenValidationCompletesWithWarnings() {
    String expectedLogMessage = "validation completed";
    int validationContextArgumentIndex = 2;

    lenient()
        .doAnswer(
            invocation -> {
              ValidationContext ctx = invocation.getArgument(validationContextArgumentIndex);
              ctx.addWarning("Test warning for logging verification");
              return null;
            })
        .when(contentValidator)
        .validate(any(), any(), any());

    BroadcastMap validMap = new BroadcastMap(Map.of(NODE_KEY, createValidNode()));

    broadcastValidator.validate(validMap, FILE_NAME);

    ILoggingEvent loggedEvent = findLogEventContaining(expectedLogMessage);
    assertEquals(Level.WARN, loggedEvent.getLevel());
    assertTrue(loggedEvent.getFormattedMessage().contains(expectedLogMessage));
    assertTrue(loggedEvent.getFormattedMessage().contains(FILE_NAME));
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

  private BroadcastNode createValidNode() {
    return new BroadcastNode(
        List.of(),
        MESSAGE_TEXT,
        RETURN_BUTTON_LABEL,
        VALID_START_AT,
        VALID_TOTAL_SENDS,
        VALID_INTERVAL);
  }
}
