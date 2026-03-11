package io.github.jawisimo.botsengine.interaction.command.commandset;

import io.github.jawisimo.botsengine.config.redis.UserStatePersistent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Command that restores the user's last dialog position.
 *
 * <p>Registers the {@code /last} command when persistent user state is enabled.
 *
 * @since 1.0
 */
@Component
@UserStatePersistent
@Order(2)
public class LastCommand implements Command {

  public static final String COMMAND_NAME = "/last";
  private static final String COMMAND_DESCRIPTION = "Return to where you left off";

  @Override
  public String getCommandName() {
    return COMMAND_NAME;
  }

  @Override
  public String getDescription() {
    return COMMAND_DESCRIPTION;
  }
}
