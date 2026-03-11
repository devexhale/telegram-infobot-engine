package io.github.jawisimo.botsengine.exception;

/**
 * Thrown when required bot configuration properties are missing.
 *
 * @since 1.0
 */
public class MissingPropertyException extends RuntimeException {

  public MissingPropertyException(String propertyName) {
    super("Required properties are missing: " + propertyName);
  }
}
