package io.github.jawisimo.botsengine.repository;

import io.github.jawisimo.botsengine.model.DialogNode;

/**
 * Provides access to dialog nodes by their identifier.
 *
 * <p>Defines a contract for retrieving {@link DialogNode} instances that form the dialog graph
 * defined in configuration files.
 *
 * @since 1.0
 */
public interface DialogRepository {

  /**
   * Returns the dialog node associated with the given identifier.
   *
   * @param nodeId the unique dialog node identifier
   * @return the corresponding {@link DialogNode}
   */
  DialogNode getNode(String nodeId);
}
