package io.github.devexhale.botengine.validator.properties;

import io.github.devexhale.botengine.diagnostics.exception.PropertiesInitializationException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PropertiesValidatorFacade {

  private static final String BLOCK_SEPARATOR = System.lineSeparator().repeat(2);
  private static final String LINE_SEPARATOR = System.lineSeparator() + "- ";

  private final List<PropertiesValidator> validators;

  public void validateAll() {
    log.info("Validating configuration properties...");

    String missingPropertiesMsg =
        formatSection(
            "Required properties are missing: ",
            validators.stream().flatMap(v -> v.findMissingProperties().stream()).toList());

    String invalidPropertiesMsg =
        formatSection(
            "Invalid properties: ",
            validators.stream().flatMap(v -> v.findInvalidProperties().stream()).toList());

    String fullMsg =
        Stream.of(missingPropertiesMsg, invalidPropertiesMsg)
            .filter(msg -> !msg.isBlank())
            .collect(Collectors.joining(BLOCK_SEPARATOR));

    if (!fullMsg.isBlank()) {
      throw new PropertiesInitializationException(BLOCK_SEPARATOR + fullMsg);
    }

    log.info("Configuration properties validated successfully");
  }

  private String formatSection(String header, List<String> items) {
    if (items.isEmpty()) {
      return "";
    }
    return header + LINE_SEPARATOR + String.join(LINE_SEPARATOR, items);
  }
}
