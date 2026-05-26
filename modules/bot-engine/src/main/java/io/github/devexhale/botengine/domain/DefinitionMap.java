package io.github.devexhale.botengine.domain;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class DefinitionMap<T> {

  private final Map<String, T> nodes;

  protected DefinitionMap(Map<String, T> nodes) {
    if (!(nodes instanceof LinkedHashMap)) {
      nodes = new LinkedHashMap<>(nodes);
    }

    this.nodes = nodes;
  }

  public T getNode(String id) {
    return nodes.get(id);
  }

  public Map<String, T> nodes() {
    return Collections.unmodifiableMap(nodes);
  }
}
