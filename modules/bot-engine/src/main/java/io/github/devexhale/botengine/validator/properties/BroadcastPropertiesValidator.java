package io.github.devexhale.botengine.validator.properties;

import io.github.devexhale.botengine.annotation.ConditionalOnBroadcastEnabled;
import io.github.devexhale.botengine.properties.BroadcastProperties;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

/**
 * Validates broadcast configuration properties when broadcast functionality is enabled.
 *
 * @since 1.0
 */
@Component
@Order(3)
@RequiredArgsConstructor
@ConditionalOnBroadcastEnabled
public class BroadcastPropertiesValidator implements PropertiesValidator {

  private static final String BROADCAST_FILE_NAME_PROPERTY = "telegram.bot.broadcast.file-name";
  private static final String BROADCAST_TIMEZONE_PROPERTY = "telegram.bot.broadcast.timezone";

  private final BroadcastProperties properties;
  private final ResourceLoader resourceLoader;

  @Override
  public List<String> findMissingProperties() {
    if (!properties.enabled()) {
      return List.of();
    }

    List<String> missingProperties = new ArrayList<>();

    String broadcastFileName = properties.fileName();
    String broadcastTimezone = properties.timezone();

    if (broadcastFileName == null || broadcastFileName.isBlank()) {
      missingProperties.add(BROADCAST_FILE_NAME_PROPERTY);
    }

    if (broadcastTimezone == null || broadcastTimezone.isBlank()) {
      missingProperties.add(BROADCAST_TIMEZONE_PROPERTY);
    }

    return missingProperties;
  }

  @Override
  @SuppressWarnings("ResultOfMethodCallIgnored")
  public List<String> findInvalidProperties() {
    if (!properties.enabled()) {
      return List.of();
    }

    List<String> invalidProperties = new ArrayList<>();

    String broadcastFileName = properties.fileName();
    String broadcastTimezone = properties.timezone();

    if (broadcastFileName != null
        && !broadcastFileName.isBlank()
        && !resourceLoader.getResource(broadcastFileName).exists()) {
      invalidProperties.add(
          BROADCAST_FILE_NAME_PROPERTY + ": file '%s' not found".formatted(broadcastFileName));
    }

    if (broadcastTimezone != null && !broadcastTimezone.isBlank()) {
      try {
        ZoneId.of(broadcastTimezone);
      } catch (DateTimeException e) {
        invalidProperties.add(
            "%s: has invalid value '%s'. Use valid ZoneId like 'Europe/Kyiv' or 'UTC'"
                .formatted(BROADCAST_TIMEZONE_PROPERTY, broadcastTimezone));
      }
    }

    return invalidProperties;
  }
}
