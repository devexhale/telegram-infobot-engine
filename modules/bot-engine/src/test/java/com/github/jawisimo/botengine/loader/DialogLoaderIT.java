package com.github.jawisimo.botengine.loader;

import static org.junit.jupiter.api.Assertions.*;

import com.github.jawisimo.botengine.exception.DialogLoadingException;
import com.github.jawisimo.botengine.interaction.content.handler.AudioMediaHandler;
import com.github.jawisimo.botengine.interaction.content.handler.PhotoMediaHandler;
import com.github.jawisimo.botengine.model.DialogMap;
import com.github.jawisimo.botengine.model.DialogNode;
import com.github.jawisimo.botengine.parser.DialogParserProvider;
import com.github.jawisimo.botengine.parser.JsonDialogParser;
import com.github.jawisimo.botengine.parser.YamlDialogParser;
import com.github.jawisimo.botengine.validator.DialogValidator;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@SpringBootTest(
    classes = {
      DialogLoader.class,
      MediaFileLoader.class,
      DialogValidator.class,
      JsonDialogParser.class,
      YamlDialogParser.class,
      DialogParserProvider.class,
      PhotoMediaHandler.class,
      AudioMediaHandler.class
    })
class DialogLoaderIT {

  private static final String VALID_DIALOG_YML = "dialog-test.yml";
  private static final String VALID_DIALOG_JSON = "dialog-test.json";
  private static final String INCORRECT_SYNTAX_YML = "incorrect-syntax-dialog.yml";
  private static final String INCORRECT_SYNTAX_JSON = "incorrect-syntax-dialog.json";
  private static final String INCORRECT_STRUCTURE_YML = "incorrect-structure-dialog.yml";
  private static final String INCORRECT_STRUCTURE_JSON = "incorrect-structure-dialog.json";
  private static final String NON_EXISTS_YML = "non-existent-dialog.yml";
  private static final String NON_EXISTS_JSON = "non-existent-dialog.json";
  private static final String PARSE_FAILED_YAML_MSG = "Failed to parse YAML dialog file";
  private static final String PARSE_FAILED_JSON_MSG = "Failed to parse JSON dialog file";
  private static final String START_NODE_ID = "/start";
  private static final String START_MESSAGE = "Почнемо нашу подорож. Оберіть тему: ";

  @MockitoBean private TelegramClient telegramClient;

  @Autowired private DialogLoader dialogLoader;

  @ParameterizedTest
  @ValueSource(strings = {VALID_DIALOG_YML, VALID_DIALOG_JSON})
  void load_shouldSuccessfullyLoadValidDialog_whenDialogFileIsValid(String fileName) {
    DialogMap dialogMap = dialogLoader.load(fileName);
    DialogNode startNode = dialogMap.getNode(START_NODE_ID);

    assertNotNull(dialogMap);
    assertTrue(dialogMap.containsNodeKey(START_NODE_ID));
    assertEquals(START_MESSAGE, startNode.message());
    assertNotNull(startNode.buttons());
    assertNotNull(startNode.content());
  }

  @ParameterizedTest
  @MethodSource("invalidDialogs")
  void load_shouldThrowException_whenDialogFileIsInvalid(String fileName, String expectedMessage) {
    DialogLoadingException exception =
        assertThrows(DialogLoadingException.class, () -> dialogLoader.load(fileName));

    assertEquals(expectedMessage, exception.getMessage());
  }

  @ParameterizedTest
  @ValueSource(strings = {NON_EXISTS_YML, NON_EXISTS_JSON})
  void load_shouldThrowException_whenDialogFileDoesNotExist(String fileName) {
    String expectedMessage = "Dialog file not found: '%s'".formatted(fileName);

    DialogLoadingException exception =
        assertThrows(DialogLoadingException.class, () -> dialogLoader.load(fileName));

    assertEquals(expectedMessage, exception.getMessage());
    assertNull(exception.getCause());
  }

  private static Stream<Arguments> invalidDialogs() {
    return Stream.of(
        Arguments.of(INCORRECT_SYNTAX_YML, PARSE_FAILED_YAML_MSG),
        Arguments.of(INCORRECT_SYNTAX_JSON, PARSE_FAILED_JSON_MSG),
        Arguments.of(INCORRECT_STRUCTURE_YML, PARSE_FAILED_YAML_MSG),
        Arguments.of(INCORRECT_STRUCTURE_JSON, PARSE_FAILED_JSON_MSG));
  }
}
