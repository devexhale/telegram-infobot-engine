package io.github.devexhale.botengine.storage.definition;

import io.github.devexhale.botengine.annotation.ConditionalOnBroadcastEnabled;
import io.github.devexhale.botengine.domain.broadcast.BroadcastMap;
import io.github.devexhale.botengine.domain.broadcast.BroadcastNode;
import io.github.devexhale.botengine.loader.DefinitionLoader;
import io.github.devexhale.botengine.properties.BroadcastProperties;
import io.github.devexhale.botengine.storage.warm.WarmableStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * In-memory implementation of {@link DefinitionStorage} for broadcast nodes.
 *
 * <p>Loads broadcast definitions from a configuration file during cache warm-up and stores them in
 * memory for fast access. Active only when broadcast functionality is enabled.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@ConditionalOnBroadcastEnabled
@Order(2)
@Slf4j
public class InMemoryBroadcastStorage implements WarmableStorage, DefinitionStorage<BroadcastNode> {

  private static final BroadcastMap EMPTY = new BroadcastMap(Map.of());

  private final BroadcastProperties properties;
  private final DefinitionLoader loader;

  private final AtomicReference<BroadcastMap> holder = new AtomicReference<>(EMPTY);

  @Override
  public void warmUp() {
    log.info("Warming up broadcast cache for file: '{}'", properties.fileName());
    BroadcastMap broadcastMap = loader.load(properties.fileName(), BroadcastMap.class);
    holder.set(broadcastMap);
    log.info("Broadcast cache warmed up successfully for file: '{}'", properties.fileName());
  }

  @Override
  public boolean isEnabled() {
    return properties.enabled();
  }

  @Override
  public BroadcastNode getNode(String nodeId) {
    return holder.get().getNode(nodeId);
  }

  @Override
  public Map<String, BroadcastNode> getMap() {
    return holder.get().nodes();
  }
}
