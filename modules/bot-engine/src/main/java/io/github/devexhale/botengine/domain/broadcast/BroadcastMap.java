package io.github.devexhale.botengine.domain.broadcast;

import io.github.devexhale.botengine.domain.DefinitionMap;
import java.util.Map;

/**
 * Represents the complete broadcast definition as a map of nodes.
 *
 * @since 1.0
 */
public final class BroadcastMap extends DefinitionMap<BroadcastNode> {

  public BroadcastMap(Map<String, BroadcastNode> nodes) {
    super(nodes);
  }
}
