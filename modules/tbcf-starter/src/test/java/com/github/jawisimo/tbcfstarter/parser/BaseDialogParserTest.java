package com.github.jawisimo.tbcfstarter.parser;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.jawisimo.tbcfstarter.exception.DialogLoadingException;
import com.github.jawisimo.tbcfstarter.interaction.node.model.DialogMap;
import java.io.InputStream;
import org.junit.jupiter.api.Test;

abstract class BaseDialogParserTest {

  protected abstract DialogParser getYamlParser();

  protected abstract String getCorrectFileName();

  protected abstract String getIncorrectSyntaxFileName();

  @Test
  void canParse_shouldReturnTrue_forCorrectExtensions() {
    assertTrue(getYamlParser().canParse(getCorrectFileName()));
  }

  @Test
  void canParse_shouldReturnFalse_forIncorrectExtensions() {
    String unsupportedFile = "dialog-test.txt";

    assertFalse(getYamlParser().canParse(unsupportedFile));
  }

  @Test
  void parse_shouldParseCorrectFilesSuccessfully() {
    InputStream is = getClass().getClassLoader().getResourceAsStream(getCorrectFileName());
    DialogMap dialogMap = getYamlParser().parse(is);

    assertNotNull(is);
    assertNotNull(dialogMap);
  }

  @Test
  void parse_shouldThrowDialogLoadingException_whenSyntaxIsIncorrect() {
    DialogParser parser = getYamlParser();
    String expected = "Failed to parse ";

    InputStream is = getClass().getClassLoader().getResourceAsStream(getIncorrectSyntaxFileName());

    DialogLoadingException exception =
        assertThrows(DialogLoadingException.class, () -> parser.parse(is));

    assertNotNull(is);
    assertTrue(exception.getMessage().contains(expected));
    assertNotNull(exception.getCause());
  }
}
