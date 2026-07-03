package io.github.devexhale.botengine.execution.broadcast;

import io.github.devexhale.botengine.annotation.ConditionalOnBroadcastEnabled;
import io.github.devexhale.botengine.domain.broadcast.BroadcastNode;
import io.github.devexhale.botengine.properties.BroadcastProperties;
import io.github.devexhale.botengine.service.SubscriberService;
import io.github.devexhale.botengine.storage.definition.DefinitionStorage;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.concurrent.*;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static io.github.devexhale.botengine.execution.broadcast.BroadcastDefaults.DEFAULT_TOTAL_SENDS;

/**
 * Plans and schedules broadcast executions based on configured timing parameters.
 *
 * <p>Reads broadcast nodes from storage and schedules them for execution at specified times with
 * configured intervals. Uses virtual threads for asynchronous broadcast execution.
 *
 * @since 1.0
 */
@Component
@ConditionalOnBroadcastEnabled
@Slf4j
public class BroadcastPlanner {

  private static final int INDEX_INCREMENT_STEP = 1;

  private final ExecutorService virtualThreadsExecutor;
  private final ScheduledExecutorService scheduledExecutorService;
  private final DefinitionStorage<BroadcastNode> broadcastStorage;
  private final BroadcastFanoutExecutor broadcastFanoutExecutor;
  private final BroadcastProperties broadcastProperties;
  private final SubscriberService subscriberService;

  public BroadcastPlanner(
      @Qualifier("botEngineVirtualThreadsExecutor") ExecutorService virtualThreadsExecutor,
      @Qualifier("botEngineScheduledExecutorService")
          ScheduledExecutorService scheduledExecutorService,
      DefinitionStorage<BroadcastNode> broadcastStorage,
      BroadcastFanoutExecutor broadcastFanoutExecutor,
      BroadcastProperties broadcastProperties,
      SubscriberService subscriberService) {
    this.virtualThreadsExecutor = virtualThreadsExecutor;
    this.scheduledExecutorService = scheduledExecutorService;
    this.broadcastStorage = broadcastStorage;
    this.broadcastFanoutExecutor = broadcastFanoutExecutor;
    this.broadcastProperties = broadcastProperties;
    this.subscriberService = subscriberService;
  }

  private ZoneId zoneId;

  /** Initializes the timezone for broadcast scheduling. */
  @PostConstruct
  public void init() {
    zoneId = ZoneId.of(broadcastProperties.timezone());
  }

  /** Schedules all broadcast nodes after the application is ready. */
  @EventListener(ApplicationReadyEvent.class)
  public void run() {
    log.info("Initializing broadcast schedules...");
    broadcastStorage.getMap().forEach(this::scheduleNode);
    log.info("Finished initializing broadcast schedules");
  }

  /**
   * Schedules all sends for a single broadcast node.
   *
   * @param nodeId the broadcast node ID
   * @param node the broadcast node to schedule
   */
  private void scheduleNode(String nodeId, BroadcastNode node) {
    int totalSends = getTotalSends(node);
    Duration interval = getInterval(node);
    LocalDateTime startAt = node.startAt();

    for (int i = 0; i < totalSends; i++) {
      scheduleSingleSend(nodeId, startAt, interval, i);
    }
  }

  /**
   * Schedules a single broadcast send at the calculated execution time.
   *
   * @param nodeId the broadcast node ID
   * @param startAt the base start time
   * @param interval the interval between sends
   * @param sendIndex the index of this send (0-based)
   */
  private void scheduleSingleSend(
      String nodeId, LocalDateTime startAt, Duration interval, int sendIndex) {
    ZonedDateTime startAtZoned = startAt.atZone(zoneId);
    ZonedDateTime executionTime = startAtZoned.plus(interval.multipliedBy(sendIndex));
    ZonedDateTime now = ZonedDateTime.now(zoneId);

    if (executionTime.isBefore(now) || executionTime.isEqual(now)) {
      logPastExecution(nodeId, sendIndex, executionTime.toLocalDateTime());
      return;
    }

    long delay = Duration.between(now, executionTime).toMillis();

    scheduledExecutorService.schedule(
        () -> executeBroadcastAsync(nodeId, sendIndex), delay, TimeUnit.MILLISECONDS);
  }

  /**
   * Submits the broadcast execution to the virtual thread executor.
   *
   * @param nodeId the broadcast node ID
   * @param sendIndex the index of this send
   */
  private void executeBroadcastAsync(String nodeId, int sendIndex) {
    try {
      virtualThreadsExecutor.execute(() -> executeBroadcast(nodeId, sendIndex));
    } catch (Exception e) {
      log.error(
          "Failed to submit broadcast task to virtual thread executor for node '{}' send #{}",
          nodeId,
          sendIndex + INDEX_INCREMENT_STEP,
          e);
    }
  }

  /**
   * Executes the broadcast by retrieving subscribers and delegating to the fanout executor.
   *
   * @param nodeId the broadcast node ID
   * @param sendIndex the index of this send
   */
  private void executeBroadcast(String nodeId, int sendIndex) {
    try {
      Set<String> subscribers = subscriberService.getAllSubscribers();

      if (subscribers.isEmpty()) {
        log.warn(
            "Broadcast node '{}' send #{} skipped: no subscribers found",
            nodeId,
            sendIndex + INDEX_INCREMENT_STEP);
        return;
      }

      broadcastFanoutExecutor.execute(nodeId, subscribers);
    } catch (Exception e) {
      log.error("Broadcast preparation failed for node '{}'", nodeId, e);
    }
  }

  private int getTotalSends(BroadcastNode node) {
    return node.totalSends() != null ? node.totalSends() : DEFAULT_TOTAL_SENDS;
  }

  private Duration getInterval(BroadcastNode node) {
    return node.interval() != null ? node.interval() : Duration.ZERO;
  }

  private void logPastExecution(String nodeId, int sendIndex, LocalDateTime executionTime) {
    log.warn(
        "Broadcast node '{}' send #{} at {} is in the past. Skipping.",
        nodeId,
        sendIndex + INDEX_INCREMENT_STEP,
        executionTime);
  }
}
