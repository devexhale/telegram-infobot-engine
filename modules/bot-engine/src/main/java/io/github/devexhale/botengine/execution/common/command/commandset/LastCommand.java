package io.github.devexhale.botengine.execution.common.command.commandset;

import io.github.devexhale.botengine.annotation.ConditionalOnBroadcastEnabled;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Command that restores the user's last dialog position.
 *
 * <p>Registers the {@code /last} command when broadcast functionality is enabled.
 *
 * @since 1.0
 */
@Component
@ConditionalOnBroadcastEnabled
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
