package com.github.jawisimo.botengine.interaction.command.commandset;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

/**
 * Represents a Telegram bot command definition.
 *
 * <p>Provides command name and description used for registration in Telegram.
 *
 * @since 1.0
 */
public interface Command {

  /**
   * Creates a {@link BotCommand} representation of this command.
   *
   * @return the Telegram bot command
   */
  default BotCommand getCommand() {
    return new BotCommand(getCommandName(), getDescription());
  }

  /**
   * Returns the command name (e.g. {@code /start}, {@code /last}).
   *
   * @return the command name
   */
  String getCommandName();

  /**
   * Returns the command description shown in Telegram.
   *
   * @return the command description
   */
  String getDescription();
}
