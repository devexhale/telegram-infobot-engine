package com.github.jawisimo.botengine.parser;

import org.junit.jupiter.api.BeforeEach;

public class YamlDialogParserTest extends BaseDialogParserTest {

  private static final String CORRECT_YAML_FILE = "dialog-test.yml";
  private static final String INCORRECT_SYNTAX_YAML_FILE = "incorrect-syntax-dialog.yml";

  private DialogParser yamlParser;

  @BeforeEach
  void init() {
    yamlParser = new YamlDialogParser();
  }

  @Override
  protected DialogParser getYamlParser() {
    return yamlParser;
  }

  @Override
  protected String getCorrectFileName() {
    return CORRECT_YAML_FILE;
  }

  @Override
  protected String getIncorrectSyntaxFileName() {
    return INCORRECT_SYNTAX_YAML_FILE;
  }
}
