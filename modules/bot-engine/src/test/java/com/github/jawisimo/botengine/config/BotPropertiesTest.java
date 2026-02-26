package com.github.jawisimo.botengine.config;

import static org.junit.jupiter.api.Assertions.*;

import com.github.jawisimo.botengine.exception.MissingPropertyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class BotPropertiesTest {

  private static final String BOT_TOKEN = "token";
  private static final String BOT_NAME = "MyBot";
  private static final String DIALOG_FILE_NAME = "dialog.yml";
  private static final String PROPERTY_EXCEPTION_PREFIX_MESSAGE =
      "Required properties are missing: ";

  @Test
  void constructor_shouldCreateBotProperties_whenAllRequiredFieldsValid() {
    int buttonsPerRow = 2;
    boolean userStatePersistent = true;

    BotProperties properties =
        new BotProperties(
            BOT_TOKEN, BOT_NAME, DIALOG_FILE_NAME, buttonsPerRow, userStatePersistent);

    assertEquals(BOT_TOKEN, properties.token());
    assertEquals(BOT_NAME, properties.name());
    assertEquals(DIALOG_FILE_NAME, properties.dialogFileName());
    assertEquals(buttonsPerRow, properties.buttonsPerRow());
    assertEquals(userStatePersistent, properties.userStatePersistent());
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"", "   "})
  void constructor_shouldThrowMissingPropertyException_whenTokenMissingOrBlank(String token) {
    int buttonsPerRow = 3;
    boolean userStatePersistent = false;
    String tokenPropertyName = "telegram.bot.token";
    String expected = PROPERTY_EXCEPTION_PREFIX_MESSAGE + tokenPropertyName;

    MissingPropertyException ex =
        assertThrows(
            MissingPropertyException.class,
            () ->
                new BotProperties(
                    token, BOT_NAME, DIALOG_FILE_NAME, buttonsPerRow, userStatePersistent));

    assertEquals(expected, ex.getMessage());
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"", "   "})
  void constructor_shouldThrowMissingPropertyException_whenDialogFileNameMissingOrBlank(
      String dialogFileName) {
    int buttonsPerRow = 1;
    boolean userStatePersistent = false;
    String dialogFilePropertyName = "telegram.bot.dialog-file-name";
    String expected = PROPERTY_EXCEPTION_PREFIX_MESSAGE + dialogFilePropertyName;

    MissingPropertyException ex =
        assertThrows(
            MissingPropertyException.class,
            () ->
                new BotProperties(
                    BOT_TOKEN, BOT_NAME, dialogFileName, buttonsPerRow, userStatePersistent));

    assertEquals(expected, ex.getMessage());
  }

  @Test
  void constructor_shouldThrowMissingPropertyException_whenTokenAndDialogFileNameInvalid() {
    String token = "   ";
    String dialogFileName = "";
    int buttonsPerRow = 4;
    boolean userStatePersistent = true;

    String expected =
        PROPERTY_EXCEPTION_PREFIX_MESSAGE
            + """
                Bot properties loading failed with 2 error(s):
                  - telegram.bot.token
                  - telegram.bot.dialog-file-name""";

    MissingPropertyException ex =
        assertThrows(
            MissingPropertyException.class,
            () ->
                new BotProperties(
                    token, BOT_NAME, dialogFileName, buttonsPerRow, userStatePersistent));

    assertEquals(expected, ex.getMessage());
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1, 11, 100})
  void constructor_shouldThrowIllegalArgumentException_whenButtonsPerRowOutOfRange(
      int buttonsPerRow) {
    boolean userStatePersistent = false;
    String expected = "telegram.bot.buttons-per-row must be between 1 and 10";

    IllegalArgumentException ex =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new BotProperties(
                    BOT_TOKEN, BOT_NAME, DIALOG_FILE_NAME, buttonsPerRow, userStatePersistent));

    assertEquals(expected, ex.getMessage());
  }

  @Test
  void
      constructor_shouldThrowMissingPropertyException_whenRequiredFieldsMissing_evenIfButtonsPerRowInvalid() {
    String dialogFileName = "   ";
    int buttonsPerRow = 0;
    boolean userStatePersistent = true;

    String expected =
        PROPERTY_EXCEPTION_PREFIX_MESSAGE
            + """
                Bot properties loading failed with 2 error(s):
                  - telegram.bot.token
                  - telegram.bot.dialog-file-name""";

    MissingPropertyException ex =
        assertThrows(
            MissingPropertyException.class,
            () ->
                new BotProperties(
                    null, BOT_NAME, dialogFileName, buttonsPerRow, userStatePersistent));

    assertEquals(expected, ex.getMessage());
  }
}
