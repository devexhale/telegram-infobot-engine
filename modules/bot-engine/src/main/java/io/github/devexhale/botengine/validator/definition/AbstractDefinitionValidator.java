package io.github.devexhale.botengine.validator.definition;

import io.github.devexhale.botengine.diagnostics.exception.DefinitionInitializationException;
import io.github.devexhale.botengine.validator.definition.util.DefinitionValidationIssueFormatter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractDefinitionValidator<T> implements DefinitionValidator<T> {

  protected void logWarnings(ValidationContext context, String message) {
    if (context.hasWarnings()) {
      log.warn(DefinitionValidationIssueFormatter.format(message, context.warnings()));
    }
  }

  protected void throwIfErrors(ValidationContext context, String message) {
    if (context.hasErrors()) {
      throw new DefinitionInitializationException(
          DefinitionValidationIssueFormatter.format(message, context.errors()));
    }
  }

  protected boolean isInvalidString(String value) {
    return value == null || value.isBlank();
  }

  protected String path(String value) {
    return "Node '%s'".formatted(value);
  }
}
