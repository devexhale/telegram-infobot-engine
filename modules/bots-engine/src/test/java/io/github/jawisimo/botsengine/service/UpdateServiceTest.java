package io.github.jawisimo.botsengine.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.jawisimo.botsengine.interaction.DialogExecutor;
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

  private static final String UPDATE_ERROR_MSG = "An error occurred during update processing";
  private static final String FAIL_PARAM = "fail";

  @Mock private DialogExecutor executor;

  @InjectMocks private UpdateService service;

  @Mock private Update update;
  @Mock private Message message;
  @Mock private CallbackQuery callbackQuery;

  @Test
  void dispatchHasMessage() {
    when(update.hasMessage()).thenReturn(true);
    when(update.getMessage()).thenReturn(message);

    service.dispatch(update);

    verify(executor).executeMessage(message);
    verify(executor, never()).executeCallback(any());
  }

  @Test
  void dispatchHasCallbackQuery() {
    when(update.hasMessage()).thenReturn(false);
    when(update.hasCallbackQuery()).thenReturn(true);
    when(update.getCallbackQuery()).thenReturn(callbackQuery);

    service.dispatch(update);

    verify(executor).executeCallback(callbackQuery);
    verify(executor, never()).executeMessage(any());
  }

  @Test
  void dispatch_shouldNotThrow_whenExecutorThrowsException_forMessage() {
    when(update.hasMessage()).thenReturn(true);
    when(update.getMessage()).thenReturn(message);
    doThrow(new RuntimeException(FAIL_PARAM)).when(executor).executeMessage(message);

    assertDoesNotThrow(() -> service.dispatch(update));

    verify(executor).executeMessage(message);
  }

  @Test
  void dispatch_shouldNotThrow_whenExecutorThrowsException_forCallback() {
    when(update.hasMessage()).thenReturn(false);
    when(update.hasCallbackQuery()).thenReturn(true);
    when(update.getCallbackQuery()).thenReturn(callbackQuery);
    doThrow(new RuntimeException(FAIL_PARAM)).when(executor).executeCallback(callbackQuery);

    assertDoesNotThrow(() -> service.dispatch(update));

    verify(executor).executeCallback(callbackQuery);
  }

  @Test
  void dispatchTypeUnsupported() {
    ListAppender<ILoggingEvent> listAppender = getListAppender();

    when(update.hasMessage()).thenReturn(false);
    when(update.hasCallbackQuery()).thenReturn(false);

    service.dispatch(update);

    ILoggingEvent event = listAppender.list.getFirst();
    assertEquals(Level.WARN, event.getLevel());
    assertTrue(event.getFormattedMessage().contains("Unsupported update type"));
  }

  @Test
  void dispatch_shouldLogError_whenExecutorThrowsException_forMessage() {
    ListAppender<ILoggingEvent> listAppender = getListAppender();

    when(update.hasMessage()).thenReturn(true);
    when(update.getMessage()).thenReturn(message);
    doThrow(new RuntimeException(FAIL_PARAM)).when(executor).executeMessage(message);

    service.dispatch(update);

    ILoggingEvent event = listAppender.list.getFirst();
    assertEquals(Level.ERROR, event.getLevel());
    assertTrue(event.getFormattedMessage().contains(UPDATE_ERROR_MSG));
    assertEquals(FAIL_PARAM, event.getThrowableProxy().getMessage());
  }

  @Test
  void dispatch_shouldLogError_whenExecutorThrowsException_forCallback() {
    ListAppender<ILoggingEvent> listAppender = getListAppender();

    when(update.hasMessage()).thenReturn(false);
    when(update.hasCallbackQuery()).thenReturn(true);
    when(update.getCallbackQuery()).thenReturn(callbackQuery);
    doThrow(new RuntimeException(FAIL_PARAM)).when(executor).executeCallback(callbackQuery);

    service.dispatch(update);

    ILoggingEvent event = listAppender.list.getFirst();
    assertEquals(Level.ERROR, event.getLevel());
    assertTrue(event.getFormattedMessage().contains(UPDATE_ERROR_MSG));
    assertEquals(FAIL_PARAM, event.getThrowableProxy().getMessage());
  }

  private ListAppender<ILoggingEvent> getListAppender() {
    Logger logger = (Logger) LoggerFactory.getLogger(UpdateService.class);
    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);
    return listAppender;
  }
}
