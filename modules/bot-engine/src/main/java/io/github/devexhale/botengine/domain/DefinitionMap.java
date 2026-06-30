package io.github.devexhale.botengine.domain;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Base abstraction for a collection of domain nodes mapped by unique string IDs.
 *
 * <p>Guarantees iteration order by storing nodes in a {@link LinkedHashMap}.
 *
 * @param <T> the type of the domain nodes
 * @since 1.0
 */
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
