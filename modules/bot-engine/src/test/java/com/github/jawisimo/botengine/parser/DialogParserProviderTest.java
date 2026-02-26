package com.github.jawisimo.botengine.parser;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.github.jawisimo.botengine.exception.DialogLoadingException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DialogParserProviderTest {

  private static final String CORRECT_YAML_FILE = "dialog.yml";
  private static final String CORRECT_JSON_FILE = "dialog.json";
  private static final String UNSUPPORTED_FILE = "dialog.txt";

  private static final String NO_PARSER_MSG_PREFIX = "No suitable parser found for file: ";

  @Mock private DialogParser yamlParser;

  @Mock private DialogParser jsonParser;

  private DialogParserProvider provider;

  @BeforeEach
  void init() {
    provider = new DialogParserProvider(List.of(yamlParser, jsonParser));
  }

  @Test
  void getParser_shouldReturnYamlParser_whenYamlFileProvided() {
    when(yamlParser.canParse(CORRECT_YAML_FILE)).thenReturn(true);
    when(jsonParser.canParse(CORRECT_YAML_FILE)).thenReturn(false);

    DialogParser result = provider.getParser(CORRECT_YAML_FILE);

    assertSame(yamlParser, result);

    verify(yamlParser).canParse(CORRECT_YAML_FILE);
    verify(jsonParser).canParse(CORRECT_YAML_FILE);
  }

  @Test
  void getParser_shouldReturnJsonParser_whenJsonFileProvided() {
    when(yamlParser.canParse(CORRECT_JSON_FILE)).thenReturn(false);
    when(jsonParser.canParse(CORRECT_JSON_FILE)).thenReturn(true);

    DialogParser result = provider.getParser(CORRECT_JSON_FILE);

    assertSame(jsonParser, result);
    verify(yamlParser).canParse(CORRECT_JSON_FILE);
    verify(jsonParser).canParse(CORRECT_JSON_FILE);
  }

  @Test
  void getParser_shouldThrowException_whenNoParserFound() {
    when(yamlParser.canParse(UNSUPPORTED_FILE)).thenReturn(false);
    when(jsonParser.canParse(UNSUPPORTED_FILE)).thenReturn(false);

    DialogLoadingException exception =
        assertThrows(DialogLoadingException.class, () -> provider.getParser(UNSUPPORTED_FILE));

    assertEquals(NO_PARSER_MSG_PREFIX + UNSUPPORTED_FILE, exception.getMessage());
  }

  @Test
  void getParser_shouldThrowException_whenMultipleParsersFound() {
    when(yamlParser.canParse(CORRECT_YAML_FILE)).thenReturn(true);
    when(jsonParser.canParse(CORRECT_YAML_FILE)).thenReturn(true);

    String expected =
        "Multiple parsers found for file: "
            + CORRECT_YAML_FILE
            + " -> ["
            + yamlParser.getClass().getSimpleName()
            + ", "
            + jsonParser.getClass().getSimpleName()
            + "]";

    DialogLoadingException exception =
        assertThrows(DialogLoadingException.class, () -> provider.getParser(CORRECT_YAML_FILE));

    assertEquals(expected, exception.getMessage());
  }

  @Test
  void getParser_shouldWorkWithSingleParser() {
    DialogParserProvider singleParserProvider = new DialogParserProvider(List.of(yamlParser));

    when(yamlParser.canParse(CORRECT_YAML_FILE)).thenReturn(true);

    DialogParser result = singleParserProvider.getParser(CORRECT_YAML_FILE);

    assertSame(yamlParser, result);
    verify(yamlParser).canParse(CORRECT_YAML_FILE);
  }

  @Test
  void getParser_shouldWorkWithEmptyParserList() {
    DialogParserProvider emptyProvider = new DialogParserProvider(List.of());
    String expected = NO_PARSER_MSG_PREFIX + CORRECT_YAML_FILE;

    DialogLoadingException exception =
        assertThrows(
            DialogLoadingException.class, () -> emptyProvider.getParser(CORRECT_YAML_FILE));

    assertEquals(expected, exception.getMessage());
  }
}
