package com.github.jawisimo.botengine.parser;

import org.junit.jupiter.api.BeforeEach;

class JsonDialogParserTest extends BaseDialogParserTest {
  private static final String CORRECT_JSON_FILE = "dialog-test.json";
  private static final String INCORRECT_SYNTAX_JSON_FILE = "incorrect-syntax-dialog.json";

  private JsonDialogParser jsonParser;

  @BeforeEach
  void init() {
    jsonParser = new JsonDialogParser();
  }

  @Override
  protected DialogParser getYamlParser() {
    return jsonParser;
  }

  @Override
  protected String getCorrectFileName() {
    return CORRECT_JSON_FILE;
  }

  @Override
  protected String getIncorrectSyntaxFileName() {
    return INCORRECT_SYNTAX_JSON_FILE;
  }
}
