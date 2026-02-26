package com.github.jawisimo.botengine.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.github.jawisimo.botengine.interaction.DialogExecutor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@ExtendWith(MockitoExtension.class)
class UpdateServiceTest {

  private static final String UPDATE_ERROR_MESSAGE = "An error occurred during update processing";
  private static final String FAIL_PARAM = "fail";

  @Mock private DialogExecutor executor;

  @InjectMocks private UpdateService service;

  @Test
  void onUpdateReceived_shouldExecuteMessage_whenUpdateHasMessage() {
    Update update = mock(Update.class);
    Message message = mock(Message.class);

    when(update.hasMessage()).thenReturn(true);
    when(update.getMessage()).thenReturn(message);

    service.onUpdateReceived(update);

    verify(executor).executeMessage(message);
    verify(executor, never()).executeCallback(any());
  }

  @Test
  void onUpdateReceived_shouldExecuteCallback_whenUpdateHasCallbackQuery() {
    Update update = mock(Update.class);
    CallbackQuery callbackQuery = mock(CallbackQuery.class);

    when(update.hasMessage()).thenReturn(false);
    when(update.hasCallbackQuery()).thenReturn(true);
    when(update.getCallbackQuery()).thenReturn(callbackQuery);

    service.onUpdateReceived(update);

    verify(executor).executeCallback(callbackQuery);
    verify(executor, never()).executeMessage(any());
  }

  @Test
  void onUpdateReceived_shouldNotInteractWithExecutor_whenUpdateTypeUnsupported() {
    Update update = mock(Update.class);

    when(update.hasMessage()).thenReturn(false);
    when(update.hasCallbackQuery()).thenReturn(false);

    service.onUpdateReceived(update);

    verifyNoInteractions(executor);
  }

  @Test
  void onUpdateReceived_shouldNotThrow_whenExecutorThrowsException_forMessage() {
    Update update = mock(Update.class);
    Message message = mock(Message.class);

    when(update.hasMessage()).thenReturn(true);
    when(update.getMessage()).thenReturn(message);
    doThrow(new RuntimeException(FAIL_PARAM)).when(executor).executeMessage(message);

    assertDoesNotThrow(() -> service.onUpdateReceived(update));

    verify(executor).executeMessage(message);
  }

  @Test
  void onUpdateReceived_shouldNotThrow_whenExecutorThrowsException_forCallback() {
    Update update = mock(Update.class);
    CallbackQuery callbackQuery = mock(CallbackQuery.class);

    when(update.hasMessage()).thenReturn(false);
    when(update.hasCallbackQuery()).thenReturn(true);
    when(update.getCallbackQuery()).thenReturn(callbackQuery);
    doThrow(new RuntimeException(FAIL_PARAM)).when(executor).executeCallback(callbackQuery);

    assertDoesNotThrow(() -> service.onUpdateReceived(update));

    verify(executor).executeCallback(callbackQuery);
  }

  @Test
  void onUpdateReceived_shouldLogWarn_whenUpdateTypeUnsupported() {
    Update update = mock(Update.class);
    ListAppender<ILoggingEvent> listAppender = getListAppender();

    when(update.hasMessage()).thenReturn(false);
    when(update.hasCallbackQuery()).thenReturn(false);

    service.onUpdateReceived(update);

    ILoggingEvent event = listAppender.list.getFirst();
    assertEquals(Level.WARN, event.getLevel());
    assertTrue(event.getFormattedMessage().contains("Unsupported update type"));
  }

  @Test
  void onUpdateReceived_shouldLogError_whenExecutorThrowsException_forMessage() {
    Update update = mock(Update.class);
    Message message = mock(Message.class);
    ListAppender<ILoggingEvent> listAppender = getListAppender();

    when(update.hasMessage()).thenReturn(true);
    when(update.getMessage()).thenReturn(message);
    doThrow(new RuntimeException(FAIL_PARAM)).when(executor).executeMessage(message);

    service.onUpdateReceived(update);

    ILoggingEvent event = listAppender.list.getFirst();
    assertEquals(Level.ERROR, event.getLevel());
    assertTrue(event.getFormattedMessage().contains(UPDATE_ERROR_MESSAGE));
    assertTrue(event.getFormattedMessage().contains(FAIL_PARAM));
  }

  @Test
  void onUpdateReceived_shouldLogError_whenExecutorThrowsException_forCallback() {
    Update update = mock(Update.class);
    CallbackQuery callbackQuery = mock(CallbackQuery.class);
    ListAppender<ILoggingEvent> listAppender = getListAppender();

    when(update.hasMessage()).thenReturn(false);
    when(update.hasCallbackQuery()).thenReturn(true);
    when(update.getCallbackQuery()).thenReturn(callbackQuery);
    doThrow(new RuntimeException(FAIL_PARAM)).when(executor).executeCallback(callbackQuery);

    service.onUpdateReceived(update);

    ILoggingEvent event = listAppender.list.getFirst();
    assertEquals(Level.ERROR, event.getLevel());
    assertTrue(event.getFormattedMessage().contains(UPDATE_ERROR_MESSAGE));
    assertTrue(event.getFormattedMessage().contains(FAIL_PARAM));
  }

  private ListAppender<ILoggingEvent> getListAppender() {
    Logger logger = (Logger) LoggerFactory.getLogger(UpdateService.class);
    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);
    return listAppender;
  }
}
