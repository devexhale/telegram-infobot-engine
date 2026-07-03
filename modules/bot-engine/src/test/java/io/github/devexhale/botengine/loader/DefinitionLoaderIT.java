package io.github.devexhale.botengine.loader;

import static org.junit.jupiter.api.Assertions.*;

import io.github.devexhale.botengine.diagnostics.exception.DefinitionInitializationException;
import io.github.devexhale.botengine.domain.dialog.DialogMap;
import io.github.devexhale.botengine.domain.dialog.DialogNode;
import io.github.devexhale.botengine.parser.MapParser;
import io.github.devexhale.botengine.parser.definition.DialogMapDefinitionReader;
import io.github.devexhale.botengine.parser.definition.MapDefinitionReaderRegistry;
import io.github.devexhale.botengine.parser.format.JsonFormatReader;
import io.github.devexhale.botengine.parser.format.YamlFormatReader;
import io.github.devexhale.botengine.validator.definition.ContentValidator;
import io.github.devexhale.botengine.validator.definition.DefinitionValidatorRegistry;
import io.github.devexhale.botengine.validator.definition.DialogValidator;
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
      DefinitionLoader.class,
      MapParser.class,
      JsonFormatReader.class,
      YamlFormatReader.class,
      DialogMapDefinitionReader.class,
      MapDefinitionReaderRegistry.class,
      ContentValidator.class,
      DialogValidator.class,
      DefinitionValidatorRegistry.class
    })
class DefinitionLoaderIT {

  private static final String START_NODE_ID = "/start";
  private static final String START_MESSAGE = "Почнемо нашу подорож. Оберіть тему: ";

  @MockitoBean private TelegramClient telegramClient;

  @Autowired private DefinitionLoader definitionLoader;

  @ParameterizedTest
  @ValueSource(strings = {"dialog-test.yml", "dialog-test.json"})
  void load_shouldSuccessfullyLoadValidDialog_whenDialogFileIsValid(String fileName) {
    DialogMap dialogMap = definitionLoader.load(fileName, DialogMap.class);
    DialogNode startNode = dialogMap.getNode(START_NODE_ID);

    assertNotNull(dialogMap);
    assertEquals(START_MESSAGE, startNode.message());
    assertNotNull(startNode.buttons());
    assertNotNull(startNode.content());
  }

  @ParameterizedTest
  @CsvSource({
    "incorrect-syntax-dialog.yml, Failed to parse file 'incorrect-syntax-dialog.yml' into 'DialogMap'",
    "incorrect-syntax-dialog.json, Failed to parse file 'incorrect-syntax-dialog.json' into 'DialogMap'"
  })
  void load_shouldThrowException_whenDialogFileHasInvalidStructure(
      String fileName, String expectedMsg) {
    DefinitionInitializationException exception =
        assertThrows(
            DefinitionInitializationException.class,
            () -> definitionLoader.load(fileName, DialogMap.class));

    assertTrue(exception.getMessage().contains(expectedMsg));
    assertNotNull(exception.getCause());
  }

  @ParameterizedTest
  @CsvSource({
    "empty-dialog.yml, Definition config file 'empty-dialog.yml' is empty",
    "empty-dialog.json, Definition config file 'empty-dialog.json' is empty"
  })
  void load_shouldThrowException_whenDialogFileIsEmpty(String fileName, String expectedMsg) {
    DefinitionInitializationException exception =
        assertThrows(
            DefinitionInitializationException.class,
            () -> definitionLoader.load(fileName, DialogMap.class));

    assertEquals(expectedMsg, exception.getMessage());
    assertNull(exception.getCause());
  }

  @ParameterizedTest
  @ValueSource(strings = {"non-existent-dialog.yml", "non-existent-dialog.json"})
  void load_shouldThrowException_whenDialogFileDoesNotExist(String fileName) {
    String expectedMsg = "Definition config file '%s' not found".formatted(fileName);

    DefinitionInitializationException exception =
        assertThrows(
            DefinitionInitializationException.class,
            () -> definitionLoader.load(fileName, DialogMap.class));

    assertEquals(expectedMsg, exception.getMessage());
    assertNull(exception.getCause());
  }

  @ParameterizedTest
  @ValueSource(strings = {"invalid-dialog.yml", "invalid-dialog.json"})
  void load_shouldThrowException_whenDialogFileFailsValidation(String fileName) {
    String expectedMsg = "Dialog map must contain node '%s'".formatted(START_NODE_ID);

    DefinitionInitializationException exception =
        assertThrows(
            DefinitionInitializationException.class,
            () -> definitionLoader.load(fileName, DialogMap.class));

    assertTrue(exception.getMessage().contains(expectedMsg));
    assertTrue(exception.getMessage().contains(fileName));
    assertNull(exception.getCause());
  }

  @Test
  void load_shouldThrowException_whenDialogFileExtensionIsUnsupported() {
    String fileName = "dialog-test.txt";
    String expectedMsg = "No config format reader found for file '%s'".formatted(fileName);

    DefinitionInitializationException exception =
        assertThrows(
            DefinitionInitializationException.class,
            () -> definitionLoader.load(fileName, DialogMap.class));

    assertEquals(expectedMsg, exception.getMessage());
  }
}
