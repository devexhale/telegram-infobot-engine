package com.github.jawisimo.botengine.interaction.command.handler;

/**
 * Handles execution of a specific bot command.
 *
 * <p>Implementations are selected by command key and invoked during command processing.
 *
 * @since 1.0
 */
public interface CommandHandler {

  /**
   * Returns the command key handled by this component (e.g. {@code /start}).
   *
   * @return the command key
   */
  String getCommandKey();

  /**
   * Handles the command for the specified chat.
   *
   * @param chatId the chat identifier
   */
  void handle(String chatId);
}
