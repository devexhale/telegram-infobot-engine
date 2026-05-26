// package io.github.jawisimo.botengine.interaction.dialog.command.handler;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;
//
// import io.github.jawisimo.botengine.execution.common.commandset.command.LastCommand;
// import io.github.jawisimo.botengine.execution.common.commandset.command.StartCommand;
// import dialog.domain.io.github.devexhale.botengine.DialogNode;
// import io.github.jawisimo.botengine.execution.common.handler.command.LastCommandHandler;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
//
// class LastCommandHandlerTest extends BaseCommandHandlerTest {
//
//  public static final String LAST_VISITED_NODE = "last-visited-node";
//
//  private LastCommandHandler handler;
//
//  @BeforeEach
//  void init() {
//    handler = new LastCommandHandler(cachedDialogRepository, userStateService, nodeExecutor);
//  }
//
//  @Test
//  void getCommandKey_shouldReturnLastCommand() {
//    assertEquals(LastCommand.COMMAND_NAME, handler.getCommandKey());
//  }
//
//  @Test
//  void getNodeKey_shouldReturnUserState_whenExists() {
//    when(userStateService.getUserStateOrDefault(CHAT_ID, StartCommand.COMMAND_NAME))
//        .thenReturn(LAST_VISITED_NODE);
//
//    String nodeKey = handler.getNodeKey(CHAT_ID);
//
//    assertEquals(LAST_VISITED_NODE, nodeKey);
//    verify(userStateService).getUserStateOrDefault(CHAT_ID, StartCommand.COMMAND_NAME);
//  }
//
//  @Test
//  void handle_shouldExecuteLastVisitedNodeAndSaveState() {
//    when(userStateService.getUserStateOrDefault(CHAT_ID, StartCommand.COMMAND_NAME))
//        .thenReturn(LAST_VISITED_NODE);
//
//    DialogNode expectedNode = mock(DialogNode.class);
//    when(cachedDialogRepository.getNode(LAST_VISITED_NODE)).thenReturn(expectedNode);
//
//    handler.handle(CHAT_ID);
//
//    verify(cachedDialogRepository).getNode(LAST_VISITED_NODE);
//    verify(nodeExecutor).execute(expectedNode, CHAT_ID);
//    verify(userStateService).saveUserState(CHAT_ID, LAST_VISITED_NODE);
//  }
//
//  @Test
//  void handle_shouldExecuteStartNode_whenNoUserState() {
//    when(userStateService.getUserStateOrDefault(CHAT_ID, StartCommand.COMMAND_NAME))
//        .thenReturn(StartCommand.COMMAND_NAME);
//
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
//    when(userStateService.getUserStateOrDefault(CHAT_ID, StartCommand.COMMAND_NAME))
//        .thenReturn(LAST_VISITED_NODE);
//    when(cachedDialogRepository.getNode(LAST_VISITED_NODE)).thenReturn(null);
//
//    handler.handle(CHAT_ID);
//
//    verify(cachedDialogRepository).getNode(LAST_VISITED_NODE);
//    verifyNoInteractions(nodeExecutor);
//    verify(userStateService, never()).saveUserState(anyString(), anyString());
//  }
//
//  @Test
//  void getNodeKey_shouldFallbackToStartCommand_whenNoState() {
//    when(userStateService.getUserStateOrDefault(CHAT_ID, StartCommand.COMMAND_NAME))
//        .thenReturn(StartCommand.COMMAND_NAME);
//
//    String nodeKey = handler.getNodeKey(CHAT_ID);
//
//    assertEquals(StartCommand.COMMAND_NAME, nodeKey);
//    verify(userStateService).getUserStateOrDefault(CHAT_ID, StartCommand.COMMAND_NAME);
//  }
// }
