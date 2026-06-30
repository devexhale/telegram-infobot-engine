package io.github.devexhale.botengine.domain.dialog;

import io.github.devexhale.botengine.domain.DefinitionMap;
import java.util.Map;

/**
 * Represents the complete dialog structure as a map of nodes.
 *
 * @since 1.0
 */
public final class DialogMap extends DefinitionMap<DialogNode> {

  public DialogMap(Map<String, DialogNode> nodes) {
    super(nodes);
  }
}
