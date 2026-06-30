package io.github.devexhale.botengine.execution.update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.devexhale.botengine.execution.dialog.DialogExecutor;
import io.github.devexhale.botengine.testsupport.logger.TestLogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@ExtendWith(MockitoExtension.class)
class UpdateDispatcherTest {

  private static final String CHAT_ID = "123456789";
  private static final Integer UPDATE_ID = 1;

  @Mock private DialogExecutor dialogExecutor;
  @Mock private ChatMemberUpdateExecutor chatMemberUpdateExecutor;
  @InjectMocks private UpdateDispatcher updateDispatcher;

  @Mock private Update update;
  @Mock private Message message;
  @Mock private CallbackQuery callbackQuery;
  @Mock private ChatMemberUpdated chatMemberUpdated;

  @BeforeEach
  void setUp() {
    lenient().when(update.getUpdateId()).thenReturn(UPDATE_ID);
  }

  @Test
  void dispatch_shouldExecuteChatMemberUpdate_whenHasMyChatMember() {
    when(update.hasMyChatMember()).thenReturn(true);
    when(update.getMyChatMember()).thenReturn(chatMemberUpdated);

    updateDispatcher.dispatch(update);

    verify(chatMemberUpdateExecutor).execute(chatMemberUpdated);
    verify(dialogExecutor, never()).executeMessage(any());
    verify(dialogExecutor, never()).executeCallback(any());
  }

  @Test
  void dispatch_shouldExecuteMessageDialog_whenHasMessage() {
    when(update.hasMyChatMember()).thenReturn(false);
    when(update.hasMessage()).thenReturn(true);
    when(update.getMessage()).thenReturn(message);
    when(message.getChatId()).thenReturn(Long.valueOf(CHAT_ID));

    updateDispatcher.dispatch(update);

    verify(dialogExecutor).executeMessage(message);
    verify(chatMemberUpdateExecutor, never()).execute(any());
  }

  @Test
  void dispatch_shouldExecuteCallbackDialog_whenHasCallbackQuery() {
    when(update.hasMyChatMember()).thenReturn(false);
    when(update.hasMessage()).thenReturn(false);
    when(update.hasCallbackQuery()).thenReturn(true);
    when(update.getCallbackQuery()).thenReturn(callbackQuery);
    when(callbackQuery.getMessage()).thenReturn(message);
    when(message.getChatId()).thenReturn(Long.valueOf(CHAT_ID));

    updateDispatcher.dispatch(update);

    verify(dialogExecutor).executeCallback(callbackQuery);
    verify(chatMemberUpdateExecutor, never()).execute(any());
  }

  @Test
  void dispatch_shouldLogWarning_whenUpdateTypeIsUnsupported() {
    when(update.hasMyChatMember()).thenReturn(false);
    when(update.hasMessage()).thenReturn(false);
    when(update.hasCallbackQuery()).thenReturn(false);

    try (TestLogCaptor logCaptor = new TestLogCaptor(UpdateDispatcher.class)) {
      updateDispatcher.dispatch(update);

      ILoggingEvent loggedEvent = logCaptor.events().getFirst();
      assertEquals(Level.WARN, loggedEvent.getLevel());
      assertTrue(loggedEvent.getFormattedMessage().contains("Unsupported update type"));
    }
  }

  @Test
  void dispatch_shouldLogError_whenProcessingFails() {
    String expectedErrorMessagePart = "An error occurred during update processing";
    String expectedUpdateId = String.valueOf(UPDATE_ID);
    RuntimeException simulatedException = new RuntimeException("Simulated error");

    when(update.hasMyChatMember()).thenReturn(false);
    when(update.hasMessage()).thenReturn(true);
    when(update.getMessage()).thenReturn(message);
    when(message.getChatId()).thenReturn(Long.valueOf(CHAT_ID));
    doThrow(simulatedException).when(dialogExecutor).executeMessage(message);

    try (TestLogCaptor logCaptor = new TestLogCaptor(UpdateDispatcher.class)) {
      updateDispatcher.dispatch(update);

      ILoggingEvent loggedEvent = logCaptor.events().getFirst();

      assertEquals(Level.ERROR, loggedEvent.getLevel());
      assertTrue(loggedEvent.getFormattedMessage().contains(expectedErrorMessagePart));
      assertTrue(loggedEvent.getFormattedMessage().contains(expectedUpdateId));
      assertTrue(loggedEvent.getFormattedMessage().contains(CHAT_ID));
    }
  }
}
