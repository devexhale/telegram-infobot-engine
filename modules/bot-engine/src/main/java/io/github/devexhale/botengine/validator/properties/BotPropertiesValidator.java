package io.github.devexhale.botengine.validator.properties;

import io.github.devexhale.botengine.properties.BotProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
@Order(1)
@RequiredArgsConstructor
public class BotPropertiesValidator implements PropertiesValidator {

  private static final String BOT_TOKEN_PROPERTY = "telegram.bot.token";
  private static final String GLOBAL_CAPACITY_PROPERTY = "telegram.bot.rate-limit.global.capacity";
  private static final String GLOBAL_RATE_PROPERTY = "telegram.bot.rate-limit.global.rate";
  private static final String GLOBAL_INTERVAL_PROPERTY = "telegram.bot.rate-limit.global.interval";
  private static final String CHAT_CAPACITY_PROPERTY = "telegram.bot.rate-limit.chat.capacity";
  private static final String CHAT_RATE_PROPERTY = "telegram.bot.rate-limit.chat.rate";
  private static final String CHAT_INTERVAL_PROPERTY = "telegram.bot.rate-limit.chat.interval";

  private final BotProperties properties;

  @Override
  public List<String> findMissingProperties() {
    List<String> missingProperties = new ArrayList<>();
    String token = properties.token();
    if (token == null || token.isBlank()) {
      missingProperties.add(BOT_TOKEN_PROPERTY);
    }
    return missingProperties;
  }

  @Override
  public List<String> findInvalidProperties() {
    List<String> invalidProperties = new ArrayList<>();
    BotProperties.RateLimit rateLimit = properties.rateLimit();

    validateAndAdd(invalidProperties, GLOBAL_CAPACITY_PROPERTY, rateLimit.global().capacity());
    validateAndAdd(invalidProperties, GLOBAL_RATE_PROPERTY, rateLimit.global().rate());
    validateAndAdd(invalidProperties, GLOBAL_INTERVAL_PROPERTY, rateLimit.global().interval());

    validateAndAdd(invalidProperties, CHAT_CAPACITY_PROPERTY, rateLimit.chat().capacity());
    validateAndAdd(invalidProperties, CHAT_RATE_PROPERTY, rateLimit.chat().rate());
    validateAndAdd(invalidProperties, CHAT_INTERVAL_PROPERTY, rateLimit.chat().interval());

    return invalidProperties;
  }

  private void validateAndAdd(List<String> errors, String property, int value) {
    if (value <= 0) {
      errors.add(property + ": must be positive, got %d".formatted(value));
    }
  }

  private void validateAndAdd(List<String> errors, String property, Duration interval) {
    if (interval != null && (interval.isZero() || interval.isNegative())) {
      errors.add(property + ": must be positive and non-zero, got %s".formatted(interval));
    }
  }
}
