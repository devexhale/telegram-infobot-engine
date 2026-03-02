package com.github.jawisimo.botengine.interaction.command.commandset;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Command that starts dialog interaction with the bot.
 *
 * <p>Registers the {@code /start} command in Telegram.
 *
 * @since 1.0
 */
@Component
@Order(1)
public class StartCommand implements Command {

  public static final String COMMAND_NAME = "/start";
  private static final String COMMAND_DESCRIPTION = "Start a dialogue with the bot";

  @Override
  public String getCommandName() {
    return COMMAND_NAME;
  }

  @Override
  public String getDescription() {
    return COMMAND_DESCRIPTION;
  }
}
