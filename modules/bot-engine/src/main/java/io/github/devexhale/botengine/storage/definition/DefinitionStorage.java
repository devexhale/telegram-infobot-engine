package io.github.devexhale.botengine.storage.definition;

import java.util.Map;

/**
 * Storage abstraction for accessing definition nodes by their identifiers.
 *
 * @param <T> the type of the definition nodes
 * @since 1.0
 */
public interface DefinitionStorage<T> {

  /**
   * Retrieves a definition node by its identifier.
   *
   * @param nodeId the node identifier
   * @return the node, or {@code null} if not found
   */
  T getNode(String nodeId);

  /**
   * Retrieves all stored nodes as a map.
   *
   * @return a map of node identifiers to nodes
   */
  Map<String, T> getMap();
}
