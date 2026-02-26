package com.github.jawisimo.botengine.interaction.command.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.github.jawisimo.botengine.interaction.command.commandset.StartCommand;
import com.github.jawisimo.botengine.interaction.node.model.DialogNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StartCommandHandlerTest extends BaseCommandHandlerTest {

  private StartCommandHandler handler;

  @BeforeEach
  void init() {
    handler = new StartCommandHandler(dialogRepository, userStateService, nodeExecutor);
  }

  @Test
  void getCommandKey_shouldReturnStartCommand() {
    assertEquals(StartCommand.COMMAND_NAME, handler.getCommandKey());
  }

  @Test
  void handle_shouldExecuteStartNodeAndSaveState() {
    DialogNode expectedNode = mock(DialogNode.class);
    when(dialogRepository.getNode(StartCommand.COMMAND_NAME)).thenReturn(expectedNode);

    handler.handle(CHAT_ID);

    verify(dialogRepository).getNode(StartCommand.COMMAND_NAME);
    verify(nodeExecutor).execute(expectedNode, CHAT_ID);
    verify(userStateService).saveUserState(CHAT_ID, StartCommand.COMMAND_NAME);
  }

  @Test
  void handle_shouldDoNothing_whenNodeNotFound() {
    when(dialogRepository.getNode(StartCommand.COMMAND_NAME)).thenReturn(null);

    handler.handle(CHAT_ID);

    verify(dialogRepository).getNode(StartCommand.COMMAND_NAME);
    verifyNoInteractions(nodeExecutor);
    verify(userStateService, never()).saveUserState(anyString(), anyString());
  }
}
