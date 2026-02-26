package com.github.jawisimo.botengine.interaction.command.commandset;

import com.github.jawisimo.botengine.annotation.UserStatePersistent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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
