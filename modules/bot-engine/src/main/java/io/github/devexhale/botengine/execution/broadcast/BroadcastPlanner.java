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
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static io.github.devexhale.botengine.execution.broadcast.BroadcastDefaults.DEFAULT_TOTAL_SENDS;

@Component
@ConditionalOnBroadcastEnabled
@RequiredArgsConstructor
@Slf4j
public class BroadcastPlanner {

  private static final int INDEX_INCREMENT_STEP = 1;

  private final Executor virtualThreadsExecutor;
  private final ScheduledExecutorService scheduledExecutorService;
  private final DefinitionStorage<BroadcastNode> broadcastStorage;
  private final BroadcastFanoutExecutor broadcastFanoutExecutor;
  private final BroadcastProperties broadcastProperties;
  private final SubscriberService subscriberService;

  private ZoneId zoneId;

  @PostConstruct
  public void init() {
    zoneId = ZoneId.of(broadcastProperties.timezone());
  }

  @EventListener(ApplicationReadyEvent.class)
  public void run() {
    log.info("Initializing broadcast schedules...");
    broadcastStorage.getMap().forEach(this::scheduleNode);
    log.info("Finished initializing broadcast schedules");
  }

  private void scheduleNode(String nodeId, BroadcastNode node) {
    int totalSends = getTotalSends(node);
    Duration interval = getInterval(node);
    LocalDateTime startAt = node.startAt();

    if (startAt == null) {
      log.warn("Broadcast node '{}' has no start_at time. Skipping.", nodeId);
      return;
    }

    for (int i = 0; i < totalSends; i++) {
      scheduleSingleSend(nodeId, startAt, interval, i);
    }
  }

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
