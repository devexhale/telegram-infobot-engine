package com.github.jawisimo.botengine.exception;

/**
 * Thrown when a dialog configuration cannot be loaded, parsed, or validated.
 *
 * @since 1.0
 */
public class DialogLoadingException extends RuntimeException {

  public DialogLoadingException(String message) {
    super(message);
  }

  public DialogLoadingException(String message, Throwable cause) {
    super(message, cause);
  }
}
