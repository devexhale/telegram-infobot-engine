package io.github.devexhale.botengine.diagnostics.exception;

/**
 * Thrown when a dialog configuration cannot be loaded, parsed, or validated.
 *
 * @since 1.0
 */
public class DefinitionInitializationException extends RuntimeException {

  public DefinitionInitializationException(String message) {
    super(message);
  }

  public DefinitionInitializationException(String message, Throwable cause) {
    super(message, cause);
  }
}
