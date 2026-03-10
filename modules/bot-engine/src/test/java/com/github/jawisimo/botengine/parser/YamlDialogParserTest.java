package com.github.jawisimo.botengine.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class YamlDialogParserTest extends BaseDialogParserTest {

  private static final String CORRECT_YML_FILE = "dialog-test.yml";
  private static final String CORRECT_YAML_FILE = "dialog-test.yaml";
  private static final String INCORRECT_SYNTAX_YAML_FILE = "incorrect-syntax-dialog.yml";

  private DialogParser yamlParser;

  @BeforeEach
  void init() {
    yamlParser = new YamlDialogParser();
  }

  @Override
  protected DialogParser getParser() {
    return yamlParser;
  }

  @Override
  protected String getCorrectFileName() {
    return CORRECT_YML_FILE;
  }

  @Override
  protected String getIncorrectSyntaxFileName() {
    return INCORRECT_SYNTAX_YAML_FILE;
  }

  @Override
  @Test
  void supports_shouldReturnTrue_whenFileExtensionIsSupported() {
    assertTrue(yamlParser.supports(CORRECT_YAML_FILE));
    assertTrue(yamlParser.supports(CORRECT_YML_FILE));
  }
}
