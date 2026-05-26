package io.github.devexhale.botengine.domain.broadcast;

import io.github.devexhale.botengine.domain.DefinitionMap;
import java.util.Map;

public final class BroadcastMap extends DefinitionMap<BroadcastNode> {

  public BroadcastMap(Map<String, BroadcastNode> nodes) {
    super(nodes);
  }
}
