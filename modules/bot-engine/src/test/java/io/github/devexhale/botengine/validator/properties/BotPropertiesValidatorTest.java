package io.github.devexhale.botengine.validator.properties;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.devexhale.botengine.properties.BotProperties;
import io.github.devexhale.botengine.properties.BotProperties.RateLimit;
import io.github.devexhale.botengine.properties.BotProperties.RateLimit.Chat;
import io.github.devexhale.botengine.properties.BotProperties.RateLimit.Global;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;

class BotPropertiesValidatorTest {

  private static final String VALID_TOKEN = "123456:ABC-DEF";
  private static final String BLANK_TOKEN = "  ";
  private static final String BOT_NAME = "BotName";
  private static final String PROP_TOKEN = "telegram.bot.token";
  private static final String PROP_GLOB_CAP = "telegram.bot.rate-limit.global.capacity";
  private static final String PROP_GLOB_RATE = "telegram.bot.rate-limit.global.rate";
  private static final String PROP_GLOB_INT = "telegram.bot.rate-limit.global.interval";
  private static final String PROP_CHAT_CAP = "telegram.bot.rate-limit.chat.capacity";
  private static final String PROP_CHAT_RATE = "telegram.bot.rate-limit.chat.rate";
  private static final String PROP_CHAT_INT = "telegram.bot.rate-limit.chat.interval";

  private static final int VALID_INT = 10;
  private static final int INVALID_INT = 0;
  private static final Duration VALID_DURATION = Duration.ofSeconds(1);
  private static final Duration ZERO_DURATION = Duration.ZERO;
  private static final Duration NEG_DURATION = Duration.ofSeconds(-1);

  @Test
  void findMissingProperties_shouldReturnTokenError_whenTokenIsNull() {
    BotPropertiesValidator validator =
        new BotPropertiesValidator(new BotProperties(null, BOT_NAME, createValidRateLimit()));
    assertTrue(validator.findMissingProperties().contains(PROP_TOKEN));
  }

  @Test
  void findMissingProperties_shouldReturnTokenError_whenTokenIsBlank() {
    BotPropertiesValidator validator =
        new BotPropertiesValidator(
            new BotProperties(BLANK_TOKEN, BOT_NAME, createValidRateLimit()));
    assertTrue(validator.findMissingProperties().contains(PROP_TOKEN));
  }

  @Test
  void findMissingProperties_shouldReturnEmpty_whenTokenIsValid() {
    BotPropertiesValidator validator =
        new BotPropertiesValidator(
            new BotProperties(VALID_TOKEN, BOT_NAME, createValidRateLimit()));
    assertTrue(validator.findMissingProperties().isEmpty());
  }

  @Test
  void findInvalidProperties_shouldReturnErrors_whenGlobalIntegersAreInvalid() {
    RateLimit rateLimit =
        new RateLimit(new Global(INVALID_INT, INVALID_INT, VALID_DURATION), createValidChat());
    BotPropertiesValidator validator =
        new BotPropertiesValidator(new BotProperties(VALID_TOKEN, BOT_NAME, rateLimit));
    List<String> errors = validator.findInvalidProperties();

    assertTrue(errors.stream().anyMatch(e -> e.contains(PROP_GLOB_CAP)));
    assertTrue(errors.stream().anyMatch(e -> e.contains(PROP_GLOB_RATE)));
  }

  @Test
  void findInvalidProperties_shouldReturnError_whenGlobalIntervalIsZero() {
    RateLimit rateLimit =
        new RateLimit(new Global(VALID_INT, VALID_INT, ZERO_DURATION), createValidChat());
    BotPropertiesValidator validator =
        new BotPropertiesValidator(new BotProperties(VALID_TOKEN, BOT_NAME, rateLimit));
    List<String> errors = validator.findInvalidProperties();

    assertTrue(errors.stream().anyMatch(e -> e.contains(PROP_GLOB_INT)));
  }

  @Test
  void findInvalidProperties_shouldReturnError_whenGlobalIntervalIsNegative() {
    RateLimit rateLimit =
        new RateLimit(new Global(VALID_INT, VALID_INT, NEG_DURATION), createValidChat());
    BotPropertiesValidator validator =
        new BotPropertiesValidator(new BotProperties(VALID_TOKEN, BOT_NAME, rateLimit));
    List<String> errors = validator.findInvalidProperties();

    assertTrue(errors.stream().anyMatch(e -> e.contains(PROP_GLOB_INT)));
  }

  @Test
  void findInvalidProperties_shouldIgnoreNullGlobalInterval() {
    RateLimit rateLimit = new RateLimit(new Global(VALID_INT, VALID_INT, null), createValidChat());
    BotPropertiesValidator validator =
        new BotPropertiesValidator(new BotProperties(VALID_TOKEN, BOT_NAME, rateLimit));
    List<String> errors = validator.findInvalidProperties();

    assertTrue(errors.stream().noneMatch(e -> e.contains(PROP_GLOB_INT)));
  }

  @Test
  void findInvalidProperties_shouldReturnErrors_whenChatIntegersAreInvalid() {
    RateLimit rateLimit =
        new RateLimit(createValidGlobal(), new Chat(INVALID_INT, INVALID_INT, VALID_DURATION));
    BotPropertiesValidator validator =
        new BotPropertiesValidator(new BotProperties(VALID_TOKEN, BOT_NAME, rateLimit));
    List<String> errors = validator.findInvalidProperties();

    assertTrue(errors.stream().anyMatch(e -> e.contains(PROP_CHAT_CAP)));
    assertTrue(errors.stream().anyMatch(e -> e.contains(PROP_CHAT_RATE)));
  }

  @Test
  void findInvalidProperties_shouldReturnError_whenChatIntervalIsZero() {
    RateLimit rateLimit =
        new RateLimit(createValidGlobal(), new Chat(VALID_INT, VALID_INT, ZERO_DURATION));
    BotPropertiesValidator validator =
        new BotPropertiesValidator(new BotProperties(VALID_TOKEN, BOT_NAME, rateLimit));
    List<String> errors = validator.findInvalidProperties();

    assertTrue(errors.stream().anyMatch(e -> e.contains(PROP_CHAT_INT)));
  }

  @Test
  void findInvalidProperties_shouldReturnError_whenChatIntervalIsNegative() {
    RateLimit rateLimit =
        new RateLimit(createValidGlobal(), new Chat(VALID_INT, VALID_INT, NEG_DURATION));
    BotPropertiesValidator validator =
        new BotPropertiesValidator(new BotProperties(VALID_TOKEN, BOT_NAME, rateLimit));
    List<String> errors = validator.findInvalidProperties();

    assertTrue(errors.stream().anyMatch(e -> e.contains(PROP_CHAT_INT)));
  }

  @Test
  void findInvalidProperties_shouldIgnoreNullChatInterval() {
    RateLimit rateLimit = new RateLimit(createValidGlobal(), new Chat(VALID_INT, VALID_INT, null));
    BotPropertiesValidator validator =
        new BotPropertiesValidator(new BotProperties(VALID_TOKEN, BOT_NAME, rateLimit));
    List<String> errors = validator.findInvalidProperties();

    assertTrue(errors.stream().noneMatch(e -> e.contains(PROP_CHAT_INT)));
  }

  @Test
  void findInvalidProperties_shouldReturnEmpty_whenAllPropertiesAreValid() {
    RateLimit rateLimit = createValidRateLimit();
    BotPropertiesValidator validator =
        new BotPropertiesValidator(new BotProperties(VALID_TOKEN, BOT_NAME, rateLimit));
    assertTrue(validator.findInvalidProperties().isEmpty());
  }

  private RateLimit createValidRateLimit() {
    return new RateLimit(createValidGlobal(), createValidChat());
  }

  private Global createValidGlobal() {
    return new Global(VALID_INT, VALID_INT, VALID_DURATION);
  }

  private Chat createValidChat() {
    return new Chat(VALID_INT, VALID_INT, VALID_DURATION);
  }
}
