package io.github.jawisimo.botsengine.exception;

/**
 * Thrown when Redis dependency or connectivity verification fails.
 *
 * @since 1.0
 */
public class RedisConnectionException extends RuntimeException {

  public RedisConnectionException(String message) {
    super(message);
  }

  public RedisConnectionException(String message, Throwable cause) {
    super(message, cause);
  }
}
