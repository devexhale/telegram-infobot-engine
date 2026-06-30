package io.github.devexhale.botengine.diagnostics.exception;

/**
 * Thrown when application properties fail to load or validate.
 *
 * @since 1.0
 */
public class PropertiesInitializationException extends RuntimeException {

  public PropertiesInitializationException(String message) {
    super(message);
  }
}
