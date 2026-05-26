// package io.github.devexhale.botengine.interaction;
//
// import static org.mockito.Mockito.any;
// import static org.mockito.Mockito.anyString;
// import static org.mockito.Mockito.inOrder;
// import static org.mockito.Mockito.never;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.verifyNoInteractions;
// import static org.mockito.Mockito.when;
//
// import io.github.devexhale.botengine.execution.dialog.DialogExecutor;
// import io.github.devexhale.botengine.execution.common.command.CommandExecutor;
// import io.github.jawisimo.botengine.execution.dialog.navigator.NavigationResultHandler;
// import io.github.jawisimo.botengine.execution.dialog.navigator.NextNodeKeyResolver;
// import io.github.jawisimo.botengine.execution.dialog.navigator.NodeRouter;
// import io.github.jawisimo.botengine.execution.dialog.navigator.dto.NavigationRequest;
// import io.github.jawisimo.botengine.execution.dialog.navigator.dto.NavigationResult;
// import io.github.devexhale.botengine.execution.support.MessageCleanupManager;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.InOrder;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
// import org.telegram.telegrambots.meta.api.objects.message.Message;
//
// @ExtendWith(MockitoExtension.class)
// class DialogExecutorTest {
//
//  private static final Long CHAT_ID = 123L;
//  private static final String USER_INPUT = "hello";
//  private static final String CALLBACK_DATA = "next_node";
//  private static final String NODE_KEY = "node_1";
//
//  @Mock private MessageCleanupManager cleanupService;
//  @Mock private NextNodeKeyResolver nextNodeKeyResolver;
//  @Mock private NodeRouter nodeRouter;
//  @Mock private NavigationResultHandler navigationResultHandler;
//  @Mock private CommandExecutor commandExecutor;
//  @Mock private Message message;
//  @Mock private CallbackQuery callbackQuery;
//
//  @InjectMocks private DialogExecutor dialogExecutor;
//
//  @Test
//  void executeMessage_shouldDeleteRedundantMessageAndReturn_whenCommandExecuted() {
//    when(message.getChatId()).thenReturn(CHAT_ID);
//    when(message.getText()).thenReturn(USER_INPUT);
//    when(commandExecutor.executeIfExists(CHAT_ID.toString(), USER_INPUT)).thenReturn(true);
//
//    dialogExecutor.executeMessage(message);
//
//    InOrder inOrder = inOrder(cleanupService, commandExecutor);
//
//    inOrder.verify(cleanupService).cleanRedundantMessage(message);
//    inOrder.verify(commandExecutor).executeIfExists(CHAT_ID.toString(), USER_INPUT);
//
//    verifyNoInteractions(nextNodeKeyResolver);
//    verifyNoInteractions(nodeRouter);
//    verifyNoInteractions(navigationResultHandler);
//    verify(cleanupService, never()).cleanLastNode(anyString());
//  }
//
//  @Test
//  void executeMessage_shouldResolveRouteAndHandle_whenCommandNotExecuted() {
//    NavigationRequest request = new NavigationRequest(USER_INPUT, NODE_KEY, false);
//    NavigationResult result = NavigationResult.SUCCESS;
//
//    when(message.getChatId()).thenReturn(CHAT_ID);
//    when(message.getText()).thenReturn(USER_INPUT);
//    when(commandExecutor.executeIfExists(CHAT_ID.toString(), USER_INPUT)).thenReturn(false);
//    when(nextNodeKeyResolver.resolve(CHAT_ID.toString(), USER_INPUT)).thenReturn(request);
//    when(nodeRouter.route(CHAT_ID.toString(), request)).thenReturn(result);
//
//    dialogExecutor.executeMessage(message);
//
//    InOrder inOrder =
//        inOrder(
//            cleanupService,
//            commandExecutor,
//            nextNodeKeyResolver,
//            nodeRouter,
//            navigationResultHandler);
//
//    inOrder.verify(cleanupService).cleanRedundantMessage(message);
//    inOrder.verify(commandExecutor).executeIfExists(CHAT_ID.toString(), USER_INPUT);
//    inOrder.verify(nextNodeKeyResolver).resolve(CHAT_ID.toString(), USER_INPUT);
//    inOrder.verify(nodeRouter).route(CHAT_ID.toString(), request);
//    inOrder.verify(navigationResultHandler).handle(CHAT_ID.toString(), request, result);
//
//    verify(cleanupService, never()).cleanLastNode(anyString());
//  }
//
//  @Test
//  void executeMessage_shouldDoNothing_whenMessageIsNull() {
//    dialogExecutor.executeMessage(null);
//
//    verifyNoInteractions(
//        cleanupService, commandExecutor, nextNodeKeyResolver, nodeRouter,
// navigationResultHandler);
//  }
//
//  @Test
//  void executeMessage_shouldDoNothing_whenChatIdIsNull() {
//    when(message.getChatId()).thenReturn(null);
//
//    dialogExecutor.executeMessage(message);
//
//    verifyNoInteractions(
//        cleanupService, commandExecutor, nextNodeKeyResolver, nodeRouter,
// navigationResultHandler);
//  }
//
//  @Test
//  void executeMessage_shouldDoNothing_whenMessageIdIsNull() {
//    when(message.getChatId()).thenReturn(CHAT_ID);
//    when(message.getMessageId()).thenReturn(null);
//
//    dialogExecutor.executeMessage(message);
//
//    verifyNoInteractions(
//        cleanupService, commandExecutor, nextNodeKeyResolver, nodeRouter,
// navigationResultHandler);
//  }
//
//  @Test
//  void executeCallback_shouldReturn_whenCommandExecuted() {
//    when(callbackQuery.getMessage()).thenReturn(message);
//    when(callbackQuery.getData()).thenReturn(CALLBACK_DATA);
//    when(message.getChatId()).thenReturn(CHAT_ID);
//    when(commandExecutor.executeIfExists(CHAT_ID.toString(), CALLBACK_DATA)).thenReturn(true);
//
//    dialogExecutor.executeCallback(callbackQuery);
//
//    verify(commandExecutor).executeIfExists(CHAT_ID.toString(), CALLBACK_DATA);
//    verifyNoInteractions(nextNodeKeyResolver);
//    verifyNoInteractions(nodeRouter);
//    verifyNoInteractions(navigationResultHandler);
//    verify(cleanupService, never()).cleanLastNode(anyString());
//    verify(cleanupService, never()).cleanRedundantMessage(any());
//  }
//
//  @Test
//  void executeCallback_shouldCreateButtonRequestRouteAndHandle_whenCommandNotExecuted() {
//    NavigationRequest request = new NavigationRequest(CALLBACK_DATA, CALLBACK_DATA, true);
//    NavigationResult result = NavigationResult.SUCCESS;
//
//    when(callbackQuery.getMessage()).thenReturn(message);
//    when(callbackQuery.getData()).thenReturn(CALLBACK_DATA);
//    when(message.getChatId()).thenReturn(CHAT_ID);
//    when(commandExecutor.executeIfExists(CHAT_ID.toString(), CALLBACK_DATA)).thenReturn(false);
//    when(nodeRouter.route(CHAT_ID.toString(), request)).thenReturn(result);
//
//    dialogExecutor.executeCallback(callbackQuery);
//
//    InOrder inOrder = inOrder(commandExecutor, nodeRouter, navigationResultHandler);
//
//    inOrder.verify(commandExecutor).executeIfExists(CHAT_ID.toString(), CALLBACK_DATA);
//    inOrder.verify(nodeRouter).route(CHAT_ID.toString(), request);
//    inOrder.verify(navigationResultHandler).handle(CHAT_ID.toString(), request, result);
//
//    verifyNoInteractions(nextNodeKeyResolver);
//    verify(cleanupService, never()).cleanLastNode(anyString());
//    verify(cleanupService, never()).cleanRedundantMessage(any());
//  }
//
//  @Test
//  void executeCallback_shouldDoNothing_whenCallbackIsNull() {
//    dialogExecutor.executeCallback(null);
//
//    verifyNoInteractions(commandExecutor, nodeRouter, navigationResultHandler);
//  }
//
//  @Test
//  void executeCallback_shouldDoNothing_whenMessageIsNull() {
//    when(callbackQuery.getMessage()).thenReturn(null);
//
//    dialogExecutor.executeCallback(callbackQuery);
//
//    verifyNoInteractions(commandExecutor, nodeRouter, navigationResultHandler);
//  }
//
//  @Test
//  void executeCallback_shouldDoNothing_whenChatIdIsNull() {
//    when(callbackQuery.getMessage()).thenReturn(message);
//    when(message.getChatId()).thenReturn(null);
//
//    dialogExecutor.executeCallback(callbackQuery);
//
//    verifyNoInteractions(commandExecutor, nodeRouter, navigationResultHandler);
//  }
// }
