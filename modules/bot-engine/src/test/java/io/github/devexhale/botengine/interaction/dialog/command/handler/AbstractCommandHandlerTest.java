// package io.github.jawisimo.botengine.interaction.dialog.command.handler;
//
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.mockito.Mockito.*;
//
// import dialog.domain.io.github.devexhale.botengine.DialogNode;
// import handler.command.common.execution.io.github.devexhale.botengine.AbstractCommandHandler;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
//
// class AbstractCommandHandlerTest extends BaseCommandHandlerTest {
//
//  private static final String NODE_KEY = "test-node-key";
//
//  private AbstractCommandHandler handler;
//
//  @BeforeEach
//  void init() {
//    handler =
//        new AbstractCommandHandler(cachedDialogRepository, userStateService, nodeExecutor) {
//          @Override
//          public String getCommandKey() {
//            return "test-command";
//          }
//
//          @Override
//          String getNodeKey(String chatId) {
//            return NODE_KEY;
//          }
//        };
//  }
//
//  @Test
//  void handle_shouldExecuteNodeAndSaveState_whenNodeFound() {
//    DialogNode expectedNode = mock(DialogNode.class);
//    when(cachedDialogRepository.getNode(NODE_KEY)).thenReturn(expectedNode);
//
//    handler.handle(CHAT_ID);
//
//    verify(cachedDialogRepository).getNode(NODE_KEY);
//    verify(nodeExecutor).execute(expectedNode, CHAT_ID);
//    verify(userStateService).saveUserState(CHAT_ID, NODE_KEY);
//  }
//
//  @Test
//  void handle_shouldDoNothing_whenNodeNotFound() {
//    when(cachedDialogRepository.getNode(NODE_KEY)).thenReturn(null);
//
//    handler.handle(CHAT_ID);
//
//    verify(cachedDialogRepository).getNode(NODE_KEY);
//    verifyNoInteractions(nodeExecutor);
//    verify(userStateService, never()).saveUserState(anyString(), anyString());
//  }
//
//  @Test
//  void getters_shouldReturnConstructorDependencies() {
//    assertEquals(cachedDialogRepository, handler.getDialogRepository());
//    assertEquals(userStateService, handler.getUserStateService());
//    assertEquals(nodeExecutor, handler.getNodeExecutor());
//  }
// }
