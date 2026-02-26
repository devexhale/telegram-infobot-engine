package com.github.jawisimo.botengine.interaction;

import com.github.jawisimo.botengine.interaction.command.CommandExecutor;
import com.github.jawisimo.botengine.interaction.node.NodeNavigator;
import com.github.jawisimo.botengine.service.MessageCleanupService;
import com.github.jawisimo.botengine.service.UserStateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DialogExecutorTest {
  private static final long CHAT_ID_LONG = 123L;
  private static final String CHAT_ID = "123";
  private static final String USER_INPUT = "hello";
  private static final String CALLBACK_DATA = "next_node";
  private static final String NEXT_NODE_KEY = "node_1";

  @Mock private MessageCleanupService cleanupService;
  @Mock private UserStateService userStateService;
  @Mock private NodeNavigator nodeNavigator;
  @Mock private CommandExecutor commandExecutor;

  @Test
  void executeMessage_shouldDeleteRedundantMessageAndReturn_whenCommandExecuted() {
    DialogExecutor dialogExecutor =
        new DialogExecutor(cleanupService, userStateService, nodeNavigator, commandExecutor);

    Message message = mock(Message.class);

    when(message.getChatId()).thenReturn(CHAT_ID_LONG);
    when(message.getText()).thenReturn(USER_INPUT);
    when(commandExecutor.executeIfExists(CHAT_ID, USER_INPUT)).thenReturn(true);

    dialogExecutor.executeMessage(message);

    InOrder inOrder = inOrder(cleanupService, commandExecutor);
    inOrder.verify(cleanupService).deleteRedundantMessage(message);
    inOrder.verify(commandExecutor).executeIfExists(CHAT_ID, USER_INPUT);
    verifyNoInteractions(nodeNavigator);
    verifyNoInteractions(userStateService);
  }

  @Test
  void executeMessage_shouldNavigateAndSaveUserState_whenCommandNotExecutedAndNodeExists() {
    DialogExecutor dialogExecutor =
        new DialogExecutor(cleanupService, userStateService, nodeNavigator, commandExecutor);

    Message message = mock(Message.class);

    when(message.getChatId()).thenReturn(CHAT_ID_LONG);
    when(message.getText()).thenReturn(USER_INPUT);
    when(commandExecutor.executeIfExists(CHAT_ID, USER_INPUT)).thenReturn(false);
    when(nodeNavigator.getNextNodeKey(CHAT_ID, USER_INPUT)).thenReturn(NEXT_NODE_KEY);
    when(nodeNavigator.navigateToNode(CHAT_ID, NEXT_NODE_KEY)).thenReturn(true);

    dialogExecutor.executeMessage(message);

    InOrder inOrder = inOrder(cleanupService, commandExecutor, nodeNavigator, userStateService);
    inOrder.verify(cleanupService).deleteRedundantMessage(message);
    inOrder.verify(commandExecutor).executeIfExists(CHAT_ID, USER_INPUT);
    inOrder.verify(nodeNavigator).getNextNodeKey(CHAT_ID, USER_INPUT);
    inOrder.verify(nodeNavigator).navigateToNode(CHAT_ID, NEXT_NODE_KEY);
    inOrder.verify(userStateService).saveUserState(CHAT_ID, NEXT_NODE_KEY);
    verify(cleanupService, never()).clearLastNode(anyString());
  }

  @Test
  void executeMessage_shouldNotSaveUserState_whenCommandNotExecutedAndNodeNotFound() {
    DialogExecutor dialogExecutor =
        new DialogExecutor(cleanupService, userStateService, nodeNavigator, commandExecutor);

    Message message = mock(Message.class);

    when(message.getChatId()).thenReturn(CHAT_ID_LONG);
    when(message.getText()).thenReturn(USER_INPUT);
    when(commandExecutor.executeIfExists(CHAT_ID, USER_INPUT)).thenReturn(false);
    when(nodeNavigator.getNextNodeKey(CHAT_ID, USER_INPUT)).thenReturn(NEXT_NODE_KEY);
    when(nodeNavigator.navigateToNode(CHAT_ID, NEXT_NODE_KEY)).thenReturn(false);

    dialogExecutor.executeMessage(message);

    verify(cleanupService).deleteRedundantMessage(message);
    verify(commandExecutor).executeIfExists(CHAT_ID, USER_INPUT);
    verify(nodeNavigator).getNextNodeKey(CHAT_ID, USER_INPUT);
    verify(nodeNavigator).navigateToNode(CHAT_ID, NEXT_NODE_KEY);
    verify(userStateService, never()).saveUserState(anyString(), anyString());
  }

  @Test
  void executeCallback_shouldClearLastNodeAndReturn_whenCommandExecuted() {
    DialogExecutor dialogExecutor =
        new DialogExecutor(cleanupService, userStateService, nodeNavigator, commandExecutor);

    CallbackQuery callbackQuery = mock(CallbackQuery.class);
    Message message = mock(Message.class);

    when(callbackQuery.getMessage()).thenReturn(message);
    when(message.getChatId()).thenReturn(CHAT_ID_LONG);
    when(callbackQuery.getData()).thenReturn(CALLBACK_DATA);
    when(commandExecutor.executeIfExists(CHAT_ID, CALLBACK_DATA)).thenReturn(true);

    dialogExecutor.executeCallback(callbackQuery);

    InOrder inOrder = inOrder(cleanupService, commandExecutor);
    inOrder.verify(cleanupService).clearLastNode(CHAT_ID);
    inOrder.verify(commandExecutor).executeIfExists(CHAT_ID, CALLBACK_DATA);
    verifyNoInteractions(nodeNavigator);
    verifyNoInteractions(userStateService);
    verify(cleanupService, never()).deleteRedundantMessage(any());
  }

  @Test
  void executeCallback_shouldNavigateAndSaveUserState_whenCommandNotExecutedAndNodeExists() {
    DialogExecutor dialogExecutor =
        new DialogExecutor(cleanupService, userStateService, nodeNavigator, commandExecutor);

    CallbackQuery callbackQuery = mock(CallbackQuery.class);
    Message message = mock(Message.class);

    when(callbackQuery.getMessage()).thenReturn(message);
    when(message.getChatId()).thenReturn(CHAT_ID_LONG);
    when(callbackQuery.getData()).thenReturn(CALLBACK_DATA);
    when(commandExecutor.executeIfExists(CHAT_ID, CALLBACK_DATA)).thenReturn(false);
    when(nodeNavigator.navigateToNode(CHAT_ID, CALLBACK_DATA)).thenReturn(true);

    dialogExecutor.executeCallback(callbackQuery);

    InOrder inOrder = inOrder(cleanupService, commandExecutor, nodeNavigator, userStateService);
    inOrder.verify(cleanupService).clearLastNode(CHAT_ID);
    inOrder.verify(commandExecutor).executeIfExists(CHAT_ID, CALLBACK_DATA);
    inOrder.verify(nodeNavigator).navigateToNode(CHAT_ID, CALLBACK_DATA);
    inOrder.verify(userStateService).saveUserState(CHAT_ID, CALLBACK_DATA);
    verify(nodeNavigator, never()).getNextNodeKey(anyString(), anyString());
    verify(cleanupService, never()).deleteRedundantMessage(any());
  }

  @Test
  void executeCallback_shouldNotSaveUserState_whenCommandNotExecutedAndNodeNotFound() {
    DialogExecutor dialogExecutor =
        new DialogExecutor(cleanupService, userStateService, nodeNavigator, commandExecutor);

    CallbackQuery callbackQuery = mock(CallbackQuery.class);
    Message message = mock(Message.class);

    when(callbackQuery.getMessage()).thenReturn(message);
    when(message.getChatId()).thenReturn(CHAT_ID_LONG);
    when(callbackQuery.getData()).thenReturn(CALLBACK_DATA);
    when(commandExecutor.executeIfExists(CHAT_ID, CALLBACK_DATA)).thenReturn(false);
    when(nodeNavigator.navigateToNode(CHAT_ID, CALLBACK_DATA)).thenReturn(false);

    dialogExecutor.executeCallback(callbackQuery);

    verify(cleanupService).clearLastNode(CHAT_ID);
    verify(commandExecutor).executeIfExists(CHAT_ID, CALLBACK_DATA);
    verify(nodeNavigator).navigateToNode(CHAT_ID, CALLBACK_DATA);
    verify(userStateService, never()).saveUserState(anyString(), anyString());
  }
}
