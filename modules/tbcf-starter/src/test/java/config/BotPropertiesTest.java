package config;

import static org.junit.jupiter.api.Assertions.*;

import com.github.jawisimo.tbcfstarter.config.BotProperties;
import com.github.jawisimo.tbcfstarter.exception.MissingPropertyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class BotPropertiesTest {

  private static final String PROPERTY_EXCEPTION_PREFIX_MESSAGE =
      "Required properties are missing: ";

  @Test
  void constructor_shouldCreateBotProperties_whenAllRequiredFieldsValid() {
    String token = "token";
    String name = "bot-name";
    String dialogFileName = "dialog.yml";
    int buttonsPerRow = 3;
    boolean userStatePersistent = true;

    BotProperties properties =
        new BotProperties(token, name, dialogFileName, buttonsPerRow, userStatePersistent);

    assertEquals(token, properties.token());
    assertEquals(name, properties.name());
    assertEquals(dialogFileName, properties.dialogFileName());
    assertEquals(buttonsPerRow, properties.buttonsPerRow());
    assertEquals(userStatePersistent, properties.userStatePersistent());
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"", "   "})
  void constructor_shouldThrowMissingPropertyException_whenTokenMissingOrBlank(String token) {
    String name = "bot-name";
    String dialogFileName = "dialog.yml";
    int buttonsPerRow = 3;
    boolean userStatePersistent = false;
    String tokenPropertyName = "telegram.bot.token";
    String expected = PROPERTY_EXCEPTION_PREFIX_MESSAGE + tokenPropertyName;

    MissingPropertyException ex =
        assertThrows(
            MissingPropertyException.class,
            () ->
                new BotProperties(token, name, dialogFileName, buttonsPerRow, userStatePersistent));

    assertEquals(expected, ex.getMessage());
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"", "   "})
  void constructor_shouldThrowMissingPropertyException_whenDialogFileNameMissingOrBlank(
      String dialogFileName) {
    String token = "token";
    String name = "bot-name";
    int buttonsPerRow = 3;
    boolean userStatePersistent = false;
    String dialogFilePropertyName = "telegram.bot.dialog-file-name";
    String expected = PROPERTY_EXCEPTION_PREFIX_MESSAGE + dialogFilePropertyName;

    MissingPropertyException ex =
        assertThrows(
            MissingPropertyException.class,
            () ->
                new BotProperties(token, name, dialogFileName, buttonsPerRow, userStatePersistent));

    assertEquals(expected, ex.getMessage());
  }

  @Test
  void constructor_shouldThrowMissingPropertyException_whenTokenAndDialogFileNameInvalid() {
    String token = "   ";
    String name = "bot-name";
    String dialogFileName = "";
    int buttonsPerRow = 3;
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
                new BotProperties(token, name, dialogFileName, buttonsPerRow, userStatePersistent));

    assertEquals(expected, ex.getMessage());
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1, 11, 100})
  void constructor_shouldThrowIllegalArgumentException_whenButtonsPerRowOutOfRange(
      int buttonsPerRow) {
    String token = "token";
    String name = "bot-name";
    String dialogFileName = "dialog.yml";
    boolean userStatePersistent = false;
    String expected = "telegram.bot.buttons-per-row must be between 1 and 10";

    IllegalArgumentException ex =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new BotProperties(token, name, dialogFileName, buttonsPerRow, userStatePersistent));

    assertEquals(expected, ex.getMessage());
  }

  @Test
  void
      constructor_shouldThrowMissingPropertyException_whenRequiredFieldsMissing_evenIfButtonsPerRowInvalid() {
    String name = "bot-name";
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
                new BotProperties(null, name, dialogFileName, buttonsPerRow, userStatePersistent));

    assertEquals(expected, ex.getMessage());
  }
}
