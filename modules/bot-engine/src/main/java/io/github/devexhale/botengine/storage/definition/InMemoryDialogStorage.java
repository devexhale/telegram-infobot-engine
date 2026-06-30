package io.github.devexhale.botengine.storage.definition;

import io.github.devexhale.botengine.domain.dialog.DialogMap;
import io.github.devexhale.botengine.domain.dialog.DialogNode;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import io.github.devexhale.botengine.loader.DefinitionLoader;
import io.github.devexhale.botengine.properties.DialogProperties;
import io.github.devexhale.botengine.storage.warm.WarmableStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * In-memory implementation of {@link DefinitionStorage} for dialog nodes.
 *
 * <p>Loads dialog definitions from a configuration file during cache warm-up and stores them in
 * memory for fast access.
 *
 * @since 1.0
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class InMemoryDialogStorage implements WarmableStorage, DefinitionStorage<DialogNode> {

  private final DialogProperties properties;
  private final DefinitionLoader loader;
  private final AtomicReference<DialogMap> holder = new AtomicReference<>();

  @Override
  public void warmUp() {
    log.info("Warming up dialog cache for file: '{}'", properties.fileName());
    DialogMap dialogMap = loader.load(properties.fileName(), DialogMap.class);
    holder.set(dialogMap);
    log.info("Dialog cache warmed up successfully for file: '{}'", properties.fileName());
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public DialogNode getNode(String nodeId) {
    return holder.get().getNode(nodeId);
  }

  @Override
  public Map<String, DialogNode> getMap() {
    return holder.get().nodes();
  }
}
