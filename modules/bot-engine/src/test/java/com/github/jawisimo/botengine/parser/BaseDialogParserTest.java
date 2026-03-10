package com.github.jawisimo.botengine.parser;

import static org.junit.jupiter.api.Assertions.*;

import com.github.jawisimo.botengine.model.DialogMap;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

abstract class BaseDialogParserTest {

  protected abstract DialogParser getParser();

  protected abstract String getCorrectFileName();

  protected abstract String getIncorrectSyntaxFileName();

  @Test
  void supports_shouldReturnTrue_whenFileExtensionIsSupported() {
    assertTrue(getParser().supports(getCorrectFileName()));
  }

  @Test
  void supports_shouldReturnFalse_whenFileExtensionIsUnsupported() {
    String unsupportedFile = "dialog-test.txt";

    assertFalse(getParser().supports(unsupportedFile));
  }

  @Test
  void parse_shouldParseDialogSuccessfully_whenFileContentIsValid() throws IOException {
    InputStream is = getClass().getClassLoader().getResourceAsStream(getCorrectFileName());

    DialogMap dialogMap = getParser().parse(is);

    assertNotNull(is);
    assertNotNull(dialogMap);
  }

  @Test
  void parse_shouldThrowException_whenFileSyntaxIsInvalid() {
    InputStream is = getClass().getClassLoader().getResourceAsStream(getIncorrectSyntaxFileName());

    Executable parseCall = () -> getParser().parse(is);

    IOException exception = assertThrows(IOException.class, parseCall);

    assertNotNull(is);
    assertNotNull(exception.getCause());
  }
}
