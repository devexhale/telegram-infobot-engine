package parser;

import com.github.jawisimo.tbcfstarter.exception.DialogLoadingException;
import com.github.jawisimo.tbcfstarter.interaction.node.model.DialogMap;
import com.github.jawisimo.tbcfstarter.parser.JsonDialogParser;
import com.github.jawisimo.tbcfstarter.parser.YamlDialogParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class DialogParserTest {

  private YamlDialogParser yamlParser;
  private JsonDialogParser jsonParser;

  private final String correctYaml = "dialog-test.yml";
  private final String correctJson = "dialog-test.json";

  @BeforeEach
  void init() {
    yamlParser = new YamlDialogParser();
    jsonParser = new JsonDialogParser();
  }

  @Test
  void canParse_shouldReturnTrue_forCorrectExtensions() {
    assertTrue(yamlParser.canParse(correctYaml));
    assertTrue(jsonParser.canParse(correctJson));
  }

  @Test
  void canParse_shouldReturnFalse_forIncorrectExtensions() {
    assertFalse(yamlParser.canParse("dialog-test.txt"));
    assertFalse(jsonParser.canParse("dialog-test.txt"));
  }

  @Test
  void parse_shouldParseCorrectFilesSuccessfully() {
    InputStream yamlStream = getClass().getClassLoader().getResourceAsStream(correctYaml);
    InputStream jsonStream = getClass().getClassLoader().getResourceAsStream(correctJson);

    assertNotNull(yamlStream, "Test YAML file should exist");
    assertNotNull(jsonStream, "Test JSON file should exist");

    DialogMap yamlMap = yamlParser.parse(yamlStream);
    DialogMap jsonMap = jsonParser.parse(jsonStream);

    assertNotNull(yamlMap, "DialogMap from YAML should not be null");
    assertNotNull(jsonMap, "DialogMap from JSON should not be null");
  }

  @Test
  void parse_shouldThrowDialogLoadingException_whenYamlSyntaxIsIncorrect() {
    String incorrectSyntaxYaml = "incorrect-syntax-dialog.yml";
    InputStream is = getClass().getClassLoader().getResourceAsStream(incorrectSyntaxYaml);
    assertNotNull(is, "Incorrect syntax YAML file should exist");

    String expected = "Failed to parse YAML dialog";

    DialogLoadingException exception =
        assertThrows(DialogLoadingException.class, () -> yamlParser.parse(is));

    assertEquals(expected, exception.getMessage());
    assertNotNull(exception.getCause(), "Cause should be set by underlying IOException");
  }

  @Test
  void parse_shouldThrowDialogLoadingException_whenJsonSyntaxIsIncorrect() {
    String incorrectSyntaxJson = "incorrect-syntax-dialog.json";
    InputStream is = getClass().getClassLoader().getResourceAsStream(incorrectSyntaxJson);
    assertNotNull(is, "Incorrect syntax JSON file should exist");

    String expected = "Failed to parse JSON dialog";

    DialogLoadingException exception =
        assertThrows(DialogLoadingException.class, () -> jsonParser.parse(is));

    assertEquals(expected, exception.getMessage());
    assertNotNull(exception.getCause(), "Cause should be set by underlying IOException");
  }
}
