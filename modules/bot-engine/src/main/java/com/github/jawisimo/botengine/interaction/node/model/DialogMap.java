package com.github.jawisimo.botengine.interaction.node.model;

import java.util.Map;
import lombok.AllArgsConstructor;

/**
 * Root dialog model representing a deserialized dialog configuration.
 *
 * <p>Contains all dialog nodes loaded from a YAML or JSON file and provides indexed access by node
 * identifier. Acts as the in-memory representation of the dialog definition used during execution.
 *
 * @since 1.0
 */
@AllArgsConstructor
public final class DialogMap {

  private Map<String, DialogNode> nodes;

  /**
   * Returns the dialog node associated with the given identifier.
   *
   * @param nodeId the dialog node identifier
   * @return the corresponding {@link DialogNode}, or {@code null} if not found
   */
  public DialogNode getNode(String nodeId) {
    return nodes.get(nodeId);
  }

  /**
   * Checks whether a node with the given identifier exists.
   *
   * @param key the dialog node identifier
   * @return {@code true} if the node exists
   */
  public boolean containsNodeKey(String key) {
    return nodes.containsKey(key);
  }
}
