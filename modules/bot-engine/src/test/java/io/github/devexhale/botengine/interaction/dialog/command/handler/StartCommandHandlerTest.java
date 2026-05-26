// package io.github.jawisimo.botengine.interaction.dialog.command.handler;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;
//
// import io.github.jawisimo.botengine.execution.common.commandset.command.StartCommand;
// import dialog.domain.io.github.devexhale.botengine.DialogNode;
// import io.github.jawisimo.botengine.execution.common.handler.command.StartCommandHandler;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
//
// class StartCommandHandlerTest extends BaseCommandHandlerTest {
//
//  private StartCommandHandler handler;
//
//  @BeforeEach
//  void init() {
//    handler = new StartCommandHandler(cachedDialogRepository, userStateService, nodeExecutor);
//  }
//
//  @Test
//  void getCommandKey_shouldReturnStartCommand() {
//    assertEquals(StartCommand.COMMAND_NAME, handler.getCommandKey());
//  }
//
//  @Test
//  void handle_shouldExecuteStartNodeAndSaveState() {
//    DialogNode expectedNode = mock(DialogNode.class);
//    when(cachedDialogRepository.getNode(StartCommand.COMMAND_NAME)).thenReturn(expectedNode);
//
//    handler.handle(CHAT_ID);
//
//    verify(cachedDialogRepository).getNode(StartCommand.COMMAND_NAME);
//    verify(nodeExecutor).execute(expectedNode, CHAT_ID);
//    verify(userStateService).saveUserState(CHAT_ID, StartCommand.COMMAND_NAME);
//  }
//
//  @Test
//  void handle_shouldDoNothing_whenNodeNotFound() {
//    when(cachedDialogRepository.getNode(StartCommand.COMMAND_NAME)).thenReturn(null);
//
//    handler.handle(CHAT_ID);
//
//    verify(cachedDialogRepository).getNode(StartCommand.COMMAND_NAME);
//    verifyNoInteractions(nodeExecutor);
//    verify(userStateService, never()).saveUserState(anyString(), anyString());
//  }
//
//  @Test
//  void getNodeKey_shouldReturnCommandKey() {
//    String nodeKey = handler.getNodeKey(CHAT_ID);
//
//    assertEquals(StartCommand.COMMAND_NAME, nodeKey);
//  }
// }
