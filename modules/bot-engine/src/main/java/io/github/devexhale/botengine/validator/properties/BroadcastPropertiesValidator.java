package io.github.devexhale.botengine.validator.properties;

import io.github.devexhale.botengine.annotation.ConditionalOnBroadcastEnabled;
import io.github.devexhale.botengine.properties.BroadcastProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

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
    List<String> missingProperties = new ArrayList<>();

    boolean broadcastEnabled = properties.enabled();
    String broadcastFileName = properties.fileName();
    String broadcastTimezone = properties.timezone();

    if (broadcastEnabled && (broadcastFileName == null || broadcastFileName.isBlank())) {
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
    List<String> invalidProperties = new ArrayList<>();

    boolean broadcastEnabled = properties.enabled();
    String broadcastFileName = properties.fileName();
    String broadcastTimezone = properties.timezone();

    if (broadcastEnabled) {
      if (broadcastFileName != null
          && !broadcastFileName.isBlank()
          && !resourceLoader.getResource(broadcastFileName).exists()
          && !resourceLoader.getResource(Paths.get(broadcastFileName).toString()).exists()) {
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
    }
    return invalidProperties;
  }
}
