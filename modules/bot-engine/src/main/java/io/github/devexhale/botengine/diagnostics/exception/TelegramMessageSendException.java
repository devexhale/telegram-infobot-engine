package io.github.devexhale.botengine.diagnostics.exception;

public class TelegramMessageSendException extends RuntimeException {

  public TelegramMessageSendException(Throwable cause) {
    super(cause);
  }
}
