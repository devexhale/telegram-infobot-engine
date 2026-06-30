package io.github.devexhale.botengine.diagnostics.exception;

/**
 * Thrown when sending a message via the Telegram Bot API fails.
 *
 * @since 1.0
 */
public class TelegramMessageSendException extends RuntimeException {

  public TelegramMessageSendException(Throwable cause) {
    super(cause);
  }
}
