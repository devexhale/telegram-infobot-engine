package com.github.jawisimo.botengine.interaction.command.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.github.jawisimo.botengine.interaction.node.model.DialogNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AbstractCommandHandlerTest extends BaseCommandHandlerTest {

  private static final String NODE_KEY = "test-node-key";

  private AbstractCommandHandler handler;

  @BeforeEach
  void init() {
    handler =
        new AbstractCommandHandler(dialogRepository, userStateService, nodeExecutor) {
          @Override
          public String getCommandKey() {
            return "test-command";
          }

          @Override
          String getNodeKey(String chatId) {
            return NODE_KEY;
          }
        };
  }

  @Test
  void handle_shouldExecuteNodeAndSaveState_whenNodeFound() {
    DialogNode expectedNode = mock(DialogNode.class);
    when(dialogRepository.getNode(NODE_KEY)).thenReturn(expectedNode);

    handler.handle(CHAT_ID);

    verify(dialogRepository).getNode(NODE_KEY);
    verify(nodeExecutor).execute(expectedNode, CHAT_ID);
    verify(userStateService).saveUserState(CHAT_ID, NODE_KEY);
  }

  @Test
  void handle_shouldDoNothing_whenNodeNotFound() {
    when(dialogRepository.getNode(NODE_KEY)).thenReturn(null);

    handler.handle(CHAT_ID);

    verify(dialogRepository).getNode(NODE_KEY);
    verifyNoInteractions(nodeExecutor);
    verify(userStateService, never()).saveUserState(anyString(), anyString());
  }

  @Test
  void getters_shouldReturnConstructorDependencies() {
    assertEquals(dialogRepository, handler.getDialogRepository());
    assertEquals(userStateService, handler.getUserStateService());
    assertEquals(nodeExecutor, handler.getNodeExecutor());
  }
}
