package io.github.devexhale.botengine.execution.dialog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.devexhale.botengine.execution.common.command.CommandExecutor;
import io.github.devexhale.botengine.execution.dialog.navigator.DialogNodeNavigator;
import io.github.devexhale.botengine.execution.support.MessageCleanupManager;
import io.github.devexhale.botengine.testsupport.logger.TestLogCaptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@ExtendWith(MockitoExtension.class)
class DialogExecutorTest {

  private static final String CHAT_ID = "123456789";
  private static final int MESSAGE_ID = 123;
  private static final String USER_INPUT = "user_input";
  private static final String CALLBACK_DATA = "callback_data";

  @Mock private MessageCleanupManager cleanupService;
  @Mock private DialogNodeNavigator dialogNodeNavigator;
  @Mock private CommandExecutor commandExecutor;

  @InjectMocks private DialogExecutor dialogExecutor;

  @Mock private Message message;
  @Mock private CallbackQuery callbackQuery;

  private TestLogCaptor logCaptor;

  @BeforeEach
  void setUp() {
    logCaptor = new TestLogCaptor(DialogExecutor.class);

    lenient().when(message.getChatId()).thenReturn(Long.valueOf(CHAT_ID));
    lenient().when(message.getMessageId()).thenReturn(MESSAGE_ID);
    lenient().when(message.getText()).thenReturn(USER_INPUT);

    lenient().when(callbackQuery.getMessage()).thenReturn(message);
    lenient().when(callbackQuery.getData()).thenReturn(CALLBACK_DATA);
  }

  @AfterEach
  void tearDown() {
    logCaptor.close();
  }

  @Test
  void executeMessage_shouldDelegateToCommandExecutor_whenCommandExists() {
    when(commandExecutor.executeIfExists(CHAT_ID, USER_INPUT)).thenReturn(true);

    dialogExecutor.executeMessage(message);

    verify(cleanupService).cleanRedundantMessage(message);
    verify(commandExecutor).executeIfExists(CHAT_ID, USER_INPUT);
    verify(dialogNodeNavigator, never()).navigateMessage(any(), any());
  }

  @Test
  void executeMessage_shouldDelegateToDialogNavigator_whenCommandDoesNotExist() {
    when(commandExecutor.executeIfExists(CHAT_ID, USER_INPUT)).thenReturn(false);

    dialogExecutor.executeMessage(message);

    verify(cleanupService).cleanRedundantMessage(message);
    verify(dialogNodeNavigator).navigateMessage(CHAT_ID, USER_INPUT);
  }

  @Test
  void executeMessage_shouldLogWarning_whenMessageIsNull() {
    dialogExecutor.executeMessage(null);
    verify(cleanupService, never()).cleanRedundantMessage(any());
    assertWarningLogged("Skipping message");
  }

  @Test
  void executeMessage_shouldLogWarning_whenChatIdIsNull() {
    when(message.getChatId()).thenReturn(null);

    dialogExecutor.executeMessage(message);

    verify(cleanupService, never()).cleanRedundantMessage(any());
    verify(commandExecutor, never()).executeIfExists(any(), any());
    verify(dialogNodeNavigator, never()).navigateMessage(any(), any());
    assertWarningLogged("Skipping message");
  }

  @Test
  void executeMessage_shouldLogWarning_whenMessageIdIsNull() {
    when(message.getMessageId()).thenReturn(null);

    dialogExecutor.executeMessage(message);

    verify(cleanupService, never()).cleanRedundantMessage(any());
    verify(commandExecutor, never()).executeIfExists(any(), any());
    verify(dialogNodeNavigator, never()).navigateMessage(any(), any());
    assertWarningLogged("Skipping message");
  }

  @Test
  void executeCallback_shouldDelegateToCommandExecutor_whenCommandExists() {
    when(commandExecutor.executeIfExists(CHAT_ID, CALLBACK_DATA)).thenReturn(true);

    dialogExecutor.executeCallback(callbackQuery);

    verify(commandExecutor).executeIfExists(CHAT_ID, CALLBACK_DATA);
    verify(dialogNodeNavigator, never()).navigateCallback(any(), any());
  }

  @Test
  void executeCallback_shouldDelegateToDialogNavigator_whenCommandDoesNotExist() {
    when(commandExecutor.executeIfExists(CHAT_ID, CALLBACK_DATA)).thenReturn(false);

    dialogExecutor.executeCallback(callbackQuery);

    verify(dialogNodeNavigator).navigateCallback(CHAT_ID, CALLBACK_DATA);
  }

  @Test
  void executeCallback_shouldLogWarning_whenCallbackQueryIsNull() {
    dialogExecutor.executeCallback(null);
    verify(commandExecutor, never()).executeIfExists(any(), any());
    assertWarningLogged("Skipping callback");
  }

  @Test
  void executeCallback_shouldLogWarning_whenMessageIsNull() {
    when(callbackQuery.getMessage()).thenReturn(null);

    dialogExecutor.executeCallback(callbackQuery);

    verify(commandExecutor, never()).executeIfExists(any(), any());
    verify(dialogNodeNavigator, never()).navigateCallback(any(), any());
    assertWarningLogged("Skipping callback");
  }

  @Test
  void executeCallback_shouldLogWarning_whenChatIdInMessageIsNull() {
    when(message.getChatId()).thenReturn(null);

    dialogExecutor.executeCallback(callbackQuery);

    verify(commandExecutor, never()).executeIfExists(any(), any());
    verify(dialogNodeNavigator, never()).navigateCallback(any(), any());
    assertWarningLogged("Skipping callback");
  }

  private void assertWarningLogged(String expectedMessagePart) {
    ILoggingEvent loggedEvent = logCaptor.events().getFirst();
    assertEquals(Level.WARN, loggedEvent.getLevel());
    assertTrue(loggedEvent.getFormattedMessage().contains(expectedMessagePart));
  }
}
