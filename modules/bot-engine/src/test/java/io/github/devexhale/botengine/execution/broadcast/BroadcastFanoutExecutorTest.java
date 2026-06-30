package io.github.devexhale.botengine.execution.broadcast;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.devexhale.botengine.diagnostics.exception.TelegramMessageSendException;
import io.github.devexhale.botengine.domain.broadcast.BroadcastNode;
import io.github.devexhale.botengine.service.SubscriberService;
import io.github.devexhale.botengine.storage.definition.DefinitionStorage;
import io.github.devexhale.botengine.testsupport.logger.TestLogCaptor;

import java.lang.reflect.Method;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BroadcastFanoutExecutorTest {

  private static final String NODE_ID = "broadcast_node_1";
  private static final String CHAT_ID_1 = "123456789";
  private static final String CHAT_ID_2 = "987654321";
  private static final String FORBIDDEN_EXCEPTION_MSG = "403 Forbidden";

  @Mock private DefinitionStorage<BroadcastNode> broadcastStorage;
  @Mock private BroadcastNodeExecutor broadcastNodeExecutor;
  @Mock private SubscriberService subscriberService;

  @InjectMocks private BroadcastFanoutExecutor broadcastFanoutExecutor;

  @Mock private BroadcastNode broadcastNode;

  private TestLogCaptor logCaptor;

  @BeforeEach
  void setUp() {
    logCaptor = new TestLogCaptor(BroadcastFanoutExecutor.class);
    lenient().when(broadcastStorage.getNode(NODE_ID)).thenReturn(broadcastNode);
  }

  @AfterEach
  void tearDown() {
    logCaptor.close();
  }

  @Test
  void execute_shouldExecuteBroadcastNodeForAllSubscribers_whenSuccessful() {
    Set<String> subscribers = Set.of(CHAT_ID_1, CHAT_ID_2);

    lenient().doNothing().when(broadcastNodeExecutor).execute(broadcastNode, CHAT_ID_1);
    lenient().doNothing().when(broadcastNodeExecutor).execute(broadcastNode, CHAT_ID_2);

    broadcastFanoutExecutor.execute(NODE_ID, subscribers);

    verify(broadcastNodeExecutor).execute(broadcastNode, CHAT_ID_1);
    verify(broadcastNodeExecutor).execute(broadcastNode, CHAT_ID_2);
    verify(subscriberService, never()).unsubscribe(any());
  }

  @Test
  void execute_shouldUnsubscribeAndLogWarning_whenForbiddenErrorWithCode403Occurs() {
    Set<String> subscribers = Set.of(CHAT_ID_1);
    TelegramMessageSendException forbiddenException =
        new TelegramMessageSendException(new Exception(FORBIDDEN_EXCEPTION_MSG));

    doThrow(forbiddenException).when(broadcastNodeExecutor).execute(broadcastNode, CHAT_ID_1);

    broadcastFanoutExecutor.execute(NODE_ID, subscribers);

    verify(subscriberService).unsubscribe(CHAT_ID_1);

    ILoggingEvent loggedEvent = logCaptor.events().getFirst();
    assertEquals(Level.WARN, loggedEvent.getLevel());
    assertTrue(
        loggedEvent
            .getFormattedMessage()
            .contains("User unsubscribed during broadcast because of 403 Forbidden"));
    assertTrue(loggedEvent.getFormattedMessage().contains(CHAT_ID_1));
  }

  @Test
  void execute_shouldUnsubscribeAndLogWarning_whenForbiddenErrorWithMessageOccurs() {
    Set<String> subscribers = Set.of(CHAT_ID_1);
    TelegramMessageSendException forbiddenException =
        new TelegramMessageSendException(new Exception("Forbidden: bot was blocked by the user"));

    doThrow(forbiddenException).when(broadcastNodeExecutor).execute(broadcastNode, CHAT_ID_1);

    broadcastFanoutExecutor.execute(NODE_ID, subscribers);

    verify(subscriberService).unsubscribe(CHAT_ID_1);

    ILoggingEvent loggedEvent = logCaptor.events().getFirst();
    assertEquals(Level.WARN, loggedEvent.getLevel());
    assertTrue(
        loggedEvent
            .getFormattedMessage()
            .contains("User unsubscribed during broadcast because of 403 Forbidden"));
    assertTrue(loggedEvent.getFormattedMessage().contains(CHAT_ID_1));
  }

  @Test
  void execute_shouldLogError_whenOtherExceptionOccurs() {
    Set<String> subscribers = Set.of(CHAT_ID_1);
    TelegramMessageSendException genericException =
        new TelegramMessageSendException(new Exception("Some other error"));

    doThrow(genericException).when(broadcastNodeExecutor).execute(broadcastNode, CHAT_ID_1);

    broadcastFanoutExecutor.execute(NODE_ID, subscribers);

    verify(subscriberService, never()).unsubscribe(CHAT_ID_1);

    ILoggingEvent loggedEvent = logCaptor.events().getFirst();
    assertEquals(Level.ERROR, loggedEvent.getLevel());
    assertTrue(loggedEvent.getFormattedMessage().contains("Failed to send broadcast node"));
    assertTrue(loggedEvent.getFormattedMessage().contains(NODE_ID));
    assertTrue(loggedEvent.getFormattedMessage().contains(CHAT_ID_1));
  }

  @Test
  void execute_shouldHandleMixedSuccessAndFailure_whenSomeSubscribersFail() {
    Set<String> subscribers = Set.of(CHAT_ID_1, CHAT_ID_2);
    TelegramMessageSendException forbiddenException =
        new TelegramMessageSendException(new Exception(FORBIDDEN_EXCEPTION_MSG));

    doThrow(forbiddenException).when(broadcastNodeExecutor).execute(broadcastNode, CHAT_ID_1);
    lenient().doNothing().when(broadcastNodeExecutor).execute(broadcastNode, CHAT_ID_2);

    broadcastFanoutExecutor.execute(NODE_ID, subscribers);

    verify(subscriberService).unsubscribe(CHAT_ID_1);
    verify(subscriberService, never()).unsubscribe(CHAT_ID_2);
    verify(broadcastNodeExecutor).execute(broadcastNode, CHAT_ID_1);
    verify(broadcastNodeExecutor).execute(broadcastNode, CHAT_ID_2);
  }

  @Test
  void execute_shouldDoNothing_whenSubscribersSetIsEmpty() {
    broadcastFanoutExecutor.execute(NODE_ID, Set.of());
    verify(broadcastNodeExecutor, never()).execute(any(), any());
    verify(subscriberService, never()).unsubscribe(any());
  }

  @Test
  void isForbiddenError_shouldCoverMsgNullBranch() throws Exception {
    Method method =
        BroadcastFanoutExecutor.class.getDeclaredMethod("isForbiddenError", Exception.class);
    method.setAccessible(true);

    Exception e =
        new Exception() {
          @Override
          public String getMessage() {
            return null;
          }
        };

    boolean result = (boolean) method.invoke(broadcastFanoutExecutor, e);

    assertFalse(result);
  }
}
