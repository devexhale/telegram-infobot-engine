package io.github.devexhale.botengine.validator.properties;

import io.github.devexhale.botengine.properties.DialogProperties;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
@Order(2)
@RequiredArgsConstructor
public class DialogPropertiesValidator implements PropertiesValidator {

  private static final String DIALOG_FILE_NAME_PROPERTY = "telegram.bot.dialog.file-name";
  private static final String BUTTONS_PER_ROW_PROPERTY = "telegram.bot.dialog.buttons-per-row";
  private static final int BUTTONS_PER_ROW_MIN_VALUE = 1;
  private static final int BUTTONS_PER_ROW_MAX_VALUE = 10;

  private final DialogProperties properties;
  private final ResourceLoader resourceLoader;

  @Override
  public List<String> findMissingProperties() {
    List<String> missingProperties = new ArrayList<>();

    String dialogFileName = properties.fileName();

    if (dialogFileName == null || dialogFileName.isBlank()) {
      missingProperties.add(DIALOG_FILE_NAME_PROPERTY);
    }

    return missingProperties;
  }

  @Override
  public List<String> findInvalidProperties() {
    List<String> invalidProperties = new ArrayList<>();

    String dialogFileName = properties.fileName();

    if (dialogFileName != null
        && !dialogFileName.isBlank()
        && !resourceLoader.getResource(dialogFileName).exists()) {
      invalidProperties.add(
          DIALOG_FILE_NAME_PROPERTY + ": file '%s' not found".formatted(dialogFileName));
    }

    int buttonsPerRow = properties.buttonsPerRow();

    if (buttonsPerRow < BUTTONS_PER_ROW_MIN_VALUE || buttonsPerRow > BUTTONS_PER_ROW_MAX_VALUE) {
      invalidProperties.add(
          "%s: must be between %d and %d, got %d"
              .formatted(
                  BUTTONS_PER_ROW_PROPERTY,
                  BUTTONS_PER_ROW_MIN_VALUE,
                  BUTTONS_PER_ROW_MAX_VALUE,
                  buttonsPerRow));
    }

    return invalidProperties;
  }
}
