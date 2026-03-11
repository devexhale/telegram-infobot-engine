package io.github.jawisimo.botsengine.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents the complete dialog structure as a map of nodes.
 *
 * <p>Nodes are stored in a {@link LinkedHashMap} to preserve definition order.
 *
 * @param nodes the dialog nodes keyed by node identifier
 * @since 1.0
 */
public record DialogMap(Map<String, DialogNode> nodes) {

  /**
   * Creates a dialog map preserving the insertion order of nodes.
   *
   * @param nodes the dialog node definitions
   */
  public DialogMap {
    if (!(nodes instanceof LinkedHashMap)) {
      nodes = new LinkedHashMap<>(nodes);
    }
  }

  /**
   * Returns a dialog node by its key.
   *
   * @param nodeId the node identifier
   * @return the dialog node or {@code null} if not found
   */
  public DialogNode getNode(String nodeId) {
    return nodes.get(nodeId);
  }

  /**
   * Checks if the dialog contains a node with the given key.
   *
   * @param key the node identifier
   * @return {@code true} if the node exists
   */
  public boolean containsNodeKey(String key) {
    return nodes.containsKey(key);
  }

  /**
   * Returns an unmodifiable view of dialog nodes.
   *
   * @return the dialog node map
   */
  public Map<String, DialogNode> nodes() {
    return Collections.unmodifiableMap(nodes);
  }
}
