package io.github.devexhale.botengine.domain.dialog;

import io.github.devexhale.botengine.domain.DefinitionMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents the complete dialog structure as a map of nodes.
 *
 * <p>Nodes are stored in a {@link LinkedHashMap} to preserve definition order.
 *
 * @since 1.0
 */
public final class DialogMap extends DefinitionMap<DialogNode> {

  public DialogMap(Map<String, DialogNode> nodes) {
    super(nodes);
  }
}
