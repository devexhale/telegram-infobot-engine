package io.github.devexhale.botengine.validator.definition;

import io.github.devexhale.botengine.diagnostics.exception.DefinitionInitializationException;
import lombok.extern.slf4j.Slf4j;

/**
 * Base implementation of {@link DefinitionValidator} providing common validation helpers.
 *
 * @param <T> the type of the definition to validate
 * @since 1.0
 */
@Slf4j
public abstract class AbstractDefinitionValidator<T> implements DefinitionValidator<T> {

  /**
   * Logs accumulated warnings from the validation context.
   *
   * @param context the validation context
   * @param message the header message for the warnings
   */
  protected void logWarnings(ValidationContext context, String message) {
    if (context.hasWarnings()) {
      log.warn(DefinitionValidationIssueFormatter.format(message, context.warnings()));
    }
  }

  /**
   * Throws an exception if the validation context contains errors.
   *
   * @param context the validation context
   * @param message the header message for the errors
   */
  protected void throwIfErrors(ValidationContext context, String message) {
    if (context.hasErrors()) {
      throw new DefinitionInitializationException(
          DefinitionValidationIssueFormatter.format(message, context.errors()));
    }
  }

  /**
   * Checks if the given string is null or blank.
   *
   * @param value the string to check
   * @return {@code true} if the string is null or blank
   */
  protected boolean isInvalidString(String value) {
    return value == null || value.isBlank();
  }

  /**
   * Formats a node identifier into a readable path string.
   *
   * @param value the node identifier
   * @return the formatted path string
   */
  protected String path(String value) {
    return "Node '%s'".formatted(value);
  }
}
