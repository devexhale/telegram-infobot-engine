package io.github.devexhale.botengine.execution.common.command.commandset;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LastCommandTest {

  private static final String EXPECTED_COMMAND_NAME = "/last";
  private static final String EXPECTED_DESCRIPTION = "Return to where you left off";

  private final LastCommand lastCommand = new LastCommand();

  @Test
  void COMMAND_NAME_constant_shouldHaveCorrectValue() {
    assertEquals(EXPECTED_COMMAND_NAME, LastCommand.COMMAND_NAME);
  }

  @Test
  void getCommandName_shouldReturnStaticCommandName() {
    assertEquals(EXPECTED_COMMAND_NAME, lastCommand.getCommandName());
  }

  @Test
  void getDescription_shouldReturnExpectedDescription() {
    assertEquals(EXPECTED_DESCRIPTION, lastCommand.getDescription());
  }
}
