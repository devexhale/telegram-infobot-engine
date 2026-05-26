package io.github.devexhale.botengine.diagnostics.exception;

/**
 * Thrown when Redis dependency or connectivity verification fails.
 *
 * @since 1.0
 */
public class RedisInitializationException extends RuntimeException {

  public RedisInitializationException(String message) {
    super(message);
  }

  public RedisInitializationException(String message, Throwable cause) {
    super(message, cause);
  }
}
