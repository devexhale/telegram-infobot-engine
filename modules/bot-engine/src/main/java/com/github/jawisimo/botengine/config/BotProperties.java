package com.github.jawisimo.botengine.config;

import com.github.jawisimo.botengine.exception.MissingPropertyException;
import com.github.jawisimo.botengine.validator.ValidationErrorFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds and validates configuration properties under the {@code telegram.bot} prefix.
 *
 * <p>Ensures required properties are present and verifies value constraints during application
 * startup. Invalid configuration results in a startup failure.
 *
 * @since 1.0
 */
@ConfigurationProperties(prefix = "telegram.bot")
@Slf4j
public record BotProperties(
    String token,
    String name,
    String dialogFileName,
    int buttonsPerRow,
    boolean userStatePersistent) {

  private static final String BOT_TOKEN = "telegram.bot.token";
  private static final String BOT_DIALOG_FILE_NAME = "telegram.bot.dialog-file-name";
  private static final String BUTTONS_PER_ROW = "telegram.bot.buttons-per-row";
  private static final int BUTTONS_PER_ROW_MIN_VALUE = 1;
  private static final int BUTTONS_PER_ROW_MAX_VALUE = 10;

  /**
   * Validates required properties and value constraints.
   *
   * @throws MissingPropertyException if required properties are missing
   * @throws IllegalArgumentException if property values are out of range
   */
  public BotProperties {
    List<String> errors = new ArrayList<>();

    if (token == null || token.isBlank()) {
      errors.add(BOT_TOKEN);
    }

    if (dialogFileName == null || dialogFileName.isBlank()) {
      errors.add(BOT_DIALOG_FILE_NAME);
    }

    if (!errors.isEmpty()) {
      String errorMessage =
          ValidationErrorFormatter.format("Bot properties loading failed", errors);
      throw new MissingPropertyException(errorMessage);
    }

    if (buttonsPerRow < BUTTONS_PER_ROW_MIN_VALUE || buttonsPerRow > BUTTONS_PER_ROW_MAX_VALUE) {
      throw new IllegalArgumentException(
          BUTTONS_PER_ROW
              + " must be between "
              + BUTTONS_PER_ROW_MIN_VALUE
              + " and "
              + BUTTONS_PER_ROW_MAX_VALUE);
    }
  }
}
