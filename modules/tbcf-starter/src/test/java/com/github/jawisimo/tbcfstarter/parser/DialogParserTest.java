package com.github.jawisimo.tbcfstarter.parser;

import com.github.jawisimo.tbcfstarter.exception.DialogLoadingException;
import com.github.jawisimo.tbcfstarter.interaction.node.model.DialogMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class DialogParserTest {

  private static final String CORRECT_YAML_FILE = "dialog-test.yml";
  private static final String CORRECT_JSON_FILE = "dialog-test.json";

  private YamlDialogParser yamlParser;
  private JsonDialogParser jsonParser;

  @BeforeEach
  void init() {
    yamlParser = new YamlDialogParser();
    jsonParser = new JsonDialogParser();
  }

  @Test
  void canParse_shouldReturnTrue_forCorrectExtensions() {
    assertTrue(yamlParser.canParse(CORRECT_YAML_FILE));
    assertTrue(jsonParser.canParse(CORRECT_JSON_FILE));
  }

  @Test
  void canParse_shouldReturnFalse_forIncorrectExtensions() {
    String unsupportedFile = "dialog-test.txt";

    assertFalse(yamlParser.canParse(unsupportedFile));
    assertFalse(jsonParser.canParse(unsupportedFile));
  }

  @Test
  void parse_shouldParseCorrectFilesSuccessfully() {
    InputStream yamlStream = getClass().getClassLoader().getResourceAsStream(CORRECT_YAML_FILE);
    InputStream jsonStream = getClass().getClassLoader().getResourceAsStream(CORRECT_JSON_FILE);
    DialogMap yamlMap = yamlParser.parse(yamlStream);
    DialogMap jsonMap = jsonParser.parse(jsonStream);

    assertNotNull(yamlStream);
    assertNotNull(jsonStream);
    assertNotNull(yamlMap);
    assertNotNull(jsonMap);
  }

  @Test
  void parse_shouldThrowDialogLoadingException_whenYamlSyntaxIsIncorrect() {
    String incorrectSyntaxYaml = "incorrect-syntax-dialog.yml";
    InputStream is = getClass().getClassLoader().getResourceAsStream(incorrectSyntaxYaml);
    String expected = "Failed to parse YAML dialog";

    DialogLoadingException exception =
        assertThrows(DialogLoadingException.class, () -> yamlParser.parse(is));

    assertNotNull(is);
    assertEquals(expected, exception.getMessage());
    assertNotNull(exception.getCause());
  }

  @Test
  void parse_shouldThrowDialogLoadingException_whenJsonSyntaxIsIncorrect() {
    String incorrectSyntaxJson = "incorrect-syntax-dialog.json";
    String expected = "Failed to parse JSON dialog";

    InputStream is = getClass().getClassLoader().getResourceAsStream(incorrectSyntaxJson);

    DialogLoadingException exception =
        assertThrows(DialogLoadingException.class, () -> jsonParser.parse(is));

    assertNotNull(is);
    assertEquals(expected, exception.getMessage());
    assertNotNull(exception.getCause());
  }
}
