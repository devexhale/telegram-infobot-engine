package io.github.devexhale.botengine.execution.common.command.commandset;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class StartCommandTest {

  private static final String EXPECTED_COMMAND_NAME = "/start";
  private static final String EXPECTED_DESCRIPTION = "Start a dialog with the bot";

  private final StartCommand startCommand = new StartCommand();

  @Test
  void COMMAND_NAME_constant_shouldHaveCorrectValue() {
    assertEquals(EXPECTED_COMMAND_NAME, StartCommand.COMMAND_NAME);
  }

  @Test
  void getCommandName_shouldReturnStaticCommandName() {
    assertEquals(EXPECTED_COMMAND_NAME, startCommand.getCommandName());
  }

  @Test
  void getDescription_shouldReturnExpectedDescription() {
    assertEquals(EXPECTED_DESCRIPTION, startCommand.getDescription());
  }
}
