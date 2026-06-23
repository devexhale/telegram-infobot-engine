package io.github.devexhale.botengine.execution.common.command.commandset;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

class CommandTest {

  private static final String COMMAND_NAME = "/test_command";
  private static final String DESCRIPTION = "Test command description";

  @Test
  void getCommand_shouldReturnBotCommand_withCorrectNameAndDescription() {
    Command command = new TestCommand();

    BotCommand result = command.getCommand();

    assertEquals(COMMAND_NAME, result.getCommand());
    assertEquals(DESCRIPTION, result.getDescription());
  }

  private static class TestCommand implements Command {

    @Override
    public String getCommandName() {
      return COMMAND_NAME;
    }

    @Override
    public String getDescription() {
      return DESCRIPTION;
    }
  }
}
