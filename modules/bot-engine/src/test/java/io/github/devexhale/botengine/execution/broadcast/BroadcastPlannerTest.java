package io.github.devexhale.botengine.execution.broadcast;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.devexhale.botengine.domain.broadcast.BroadcastNode;
import io.github.devexhale.botengine.properties.BroadcastProperties;
import io.github.devexhale.botengine.service.SubscriberService;
import io.github.devexhale.botengine.storage.definition.DefinitionStorage;
import io.github.devexhale.botengine.testsupport.logger.TestLogCaptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BroadcastPlannerTest {

  private static final String NODE_ID = "broadcast_node_1";
  private static final String TIMEZONE = "Europe/Kiev";
  private static final String CHAT_ID = "123456789";

  private static final int FIRST_SEND_INDEX = 0;
  private static final int SINGLE_SEND = 1;
  private static final int CONFIGURED_SENDS = 3;
  private static final int DAY_OFFSET = 1;

  private static final String SCHEDULE_SINGLE_SEND_METHOD = "scheduleSingleSend";
  private static final String EXECUTE_BROADCAST_METHOD = "executeBroadcast";

  private static final Duration ONE_HOUR_INTERVAL = Duration.ofHours(1);

  @Mock private ScheduledExecutorService scheduledExecutorService;
  @Mock private DefinitionStorage<BroadcastNode> broadcastStorage;
  @Mock private BroadcastFanoutExecutor broadcastFanoutExecutor;
  @Mock private BroadcastProperties broadcastProperties;
  @Mock private SubscriberService subscriberService;

  @InjectMocks private BroadcastPlanner broadcastPlanner;

  @Mock private BroadcastNode broadcastNode;

  private TestLogCaptor logCaptor;

  @BeforeEach
  void setUp() {
    logCaptor = new TestLogCaptor(BroadcastPlanner.class);

    lenient().when(broadcastProperties.timezone()).thenReturn(TIMEZONE);
    lenient()
        .when(
            scheduledExecutorService.schedule(
                any(Runnable.class), anyLong(), eq(TimeUnit.MILLISECONDS)))
        .thenAnswer(invocation -> null);

    broadcastPlanner.init();
  }

  @AfterEach
  void tearDown() {
    logCaptor.close();
  }

  @Test
  void run_shouldScheduleAllNodes_fromBroadcastStorage() {
    LocalDateTime futureStartAt = LocalDateTime.now().plusDays(DAY_OFFSET);

    when(broadcastStorage.getMap()).thenReturn(Map.of(NODE_ID, broadcastNode));
    when(broadcastNode.startAt()).thenReturn(futureStartAt);

    lenient().when(broadcastNode.totalSends()).thenReturn(SINGLE_SEND);
    lenient().when(broadcastNode.interval()).thenReturn(Duration.ZERO);

    broadcastPlanner.run();

    verify(scheduledExecutorService)
        .schedule(any(Runnable.class), anyLong(), eq(TimeUnit.MILLISECONDS));

    ILoggingEvent initLog = logCaptor.events().getFirst();
    assertEquals(Level.INFO, initLog.getLevel());
    assertTrue(initLog.getFormattedMessage().contains("Initializing broadcast schedules"));
  }

  @Test
  void run_shouldScheduleConfiguredNumberOfSends() {
    LocalDateTime futureStartAt = LocalDateTime.now().plusDays(DAY_OFFSET);

    when(broadcastStorage.getMap()).thenReturn(Map.of(NODE_ID, broadcastNode));
    when(broadcastNode.startAt()).thenReturn(futureStartAt);
    when(broadcastNode.totalSends()).thenReturn(CONFIGURED_SENDS);
    when(broadcastNode.interval()).thenReturn(ONE_HOUR_INTERVAL);

    broadcastPlanner.run();

    verify(scheduledExecutorService, times(CONFIGURED_SENDS))
        .schedule(any(Runnable.class), anyLong(), eq(TimeUnit.MILLISECONDS));
  }

  @Test
  void scheduleSingleSend_shouldLogWarningAndSkip_whenExecutionTimeIsInPast()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    LocalDateTime pastTime = LocalDateTime.now().minusDays(DAY_OFFSET);

    Method method =
        broadcastPlanner
            .getClass()
            .getDeclaredMethod(
                SCHEDULE_SINGLE_SEND_METHOD,
                String.class,
                LocalDateTime.class,
                Duration.class,
                int.class);
    method.setAccessible(true);
    method.invoke(broadcastPlanner, NODE_ID, pastTime, Duration.ZERO, FIRST_SEND_INDEX);

    verify(scheduledExecutorService, never()).schedule((Runnable) any(), anyLong(), any());

    ILoggingEvent loggedEvent = logCaptor.events().getFirst();
    assertEquals(Level.WARN, loggedEvent.getLevel());
    assertTrue(loggedEvent.getFormattedMessage().contains("is in the past"));
  }

  @Test
  void scheduleSingleSend_shouldScheduleExecution_whenExecutionTimeIsInFuture()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    LocalDateTime futureTime = LocalDateTime.now().plusDays(DAY_OFFSET);

    Method method =
        broadcastPlanner
            .getClass()
            .getDeclaredMethod(
                SCHEDULE_SINGLE_SEND_METHOD,
                String.class,
                LocalDateTime.class,
                Duration.class,
                int.class);
    method.setAccessible(true);
    method.invoke(broadcastPlanner, NODE_ID, futureTime, Duration.ZERO, FIRST_SEND_INDEX);

    verify(scheduledExecutorService)
        .schedule(any(Runnable.class), anyLong(), eq(TimeUnit.MILLISECONDS));
  }

  @Test
  void executeBroadcast_shouldLogWarningAndSkip_whenNoSubscribersFound()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    when(subscriberService.getAllSubscribers()).thenReturn(Set.of());

    Method method =
        broadcastPlanner
            .getClass()
            .getDeclaredMethod(EXECUTE_BROADCAST_METHOD, String.class, int.class);
    method.setAccessible(true);
    method.invoke(broadcastPlanner, NODE_ID, FIRST_SEND_INDEX);

    verify(broadcastFanoutExecutor, never()).execute(any(), any());

    ILoggingEvent loggedEvent = logCaptor.events().getFirst();
    assertEquals(Level.WARN, loggedEvent.getLevel());
    assertTrue(loggedEvent.getFormattedMessage().contains("no subscribers found"));
  }

  @Test
  void executeBroadcast_shouldExecuteFanout_whenSubscribersExist()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    when(subscriberService.getAllSubscribers()).thenReturn(Set.of(CHAT_ID));

    Method method =
        broadcastPlanner
            .getClass()
            .getDeclaredMethod(EXECUTE_BROADCAST_METHOD, String.class, int.class);
    method.setAccessible(true);
    method.invoke(broadcastPlanner, NODE_ID, FIRST_SEND_INDEX);

    verify(broadcastFanoutExecutor).execute(NODE_ID, Set.of(CHAT_ID));
  }

  @Test
  void executeBroadcast_shouldLogError_whenFanoutExecutionFails()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    RuntimeException fanoutException = new RuntimeException("Fanout fail");

    when(subscriberService.getAllSubscribers()).thenReturn(Set.of(CHAT_ID));
    doThrow(fanoutException).when(broadcastFanoutExecutor).execute(NODE_ID, Set.of(CHAT_ID));

    Method method =
        broadcastPlanner
            .getClass()
            .getDeclaredMethod(EXECUTE_BROADCAST_METHOD, String.class, int.class);
    method.setAccessible(true);
    method.invoke(broadcastPlanner, NODE_ID, FIRST_SEND_INDEX);

    ILoggingEvent loggedEvent = logCaptor.events().getFirst();
    assertEquals(Level.ERROR, loggedEvent.getLevel());
    assertTrue(loggedEvent.getFormattedMessage().contains("Broadcast preparation failed"));
  }
}
