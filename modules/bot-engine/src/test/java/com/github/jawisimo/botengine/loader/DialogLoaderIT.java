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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

  private static final String START_NODE_ID = "/start";
  private static final String START_MESSAGE = "Почнемо нашу подорож. Оберіть тему: ";

  @MockitoBean private TelegramClient telegramClient;

  @Autowired private DialogLoader dialogLoader;

  @ParameterizedTest
  @ValueSource(strings = {"dialog-test.yml", "dialog-test.json"})
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
  @CsvSource({
    "incorrect-syntax-dialog.yml, Failed to load dialog file: 'incorrect-syntax-dialog.yml'",
    "incorrect-syntax-dialog.json, Failed to load dialog file: 'incorrect-syntax-dialog.json'"
  })
  void load_shouldThrowException_whenDialogFileHasInvalidStructure(
      String fileName, String expectedMsg) {
    DialogLoadingException exception =
        assertThrows(DialogLoadingException.class, () -> dialogLoader.load(fileName));

    assertEquals(expectedMsg, exception.getMessage());
    assertNotNull(exception.getCause());
  }

  @ParameterizedTest
  @CsvSource({
    "empty-dialog.yml, Dialog file is empty: 'empty-dialog.yml'",
    "empty-dialog.json, Dialog file is empty: 'empty-dialog.json'"
  })
  void load_shouldThrowException_whenDialogFileIsEmpty(String fileName, String expectedMsg) {
    DialogLoadingException exception =
        assertThrows(DialogLoadingException.class, () -> dialogLoader.load(fileName));

    assertEquals(expectedMsg, exception.getMessage());
    assertNull(exception.getCause());
  }

  @ParameterizedTest
  @ValueSource(strings = {"non-existent-dialog.yml", "non-existent-dialog.json"})
  void load_shouldThrowException_whenDialogFileDoesNotExist(String fileName) {
    String expectedMsg = "Dialog file not found: '%s'".formatted(fileName);

    DialogLoadingException exception =
        assertThrows(DialogLoadingException.class, () -> dialogLoader.load(fileName));

    assertEquals(expectedMsg, exception.getMessage());
    assertNull(exception.getCause());
  }

  @ParameterizedTest
  @ValueSource(strings = {"invalid-dialog.yml", "invalid-dialog.json"})
  void load_shouldThrowException_whenDialogFileFailsValidation(String fileName) {
    String expectedMsg =
        "Dialog must contain node '%s' in file '%s'".formatted(START_NODE_ID, fileName);

    DialogLoadingException exception =
        assertThrows(DialogLoadingException.class, () -> dialogLoader.load(fileName));

    boolean contains = exception.getMessage().contains(expectedMsg);

    assertTrue(contains);
    assertNull(exception.getCause());
  }

  @Test
  void load_shouldThrowException_whenDialogFileExtensionIsUnsupported() {
    DialogLoadingException exception =
        assertThrows(DialogLoadingException.class, () -> dialogLoader.load("dialog-test.txt"));

    assertNotNull(exception.getMessage());
  }
}
