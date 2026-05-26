package io.github.devexhale.botengine.storage.definition;

import java.util.Map;

public interface DefinitionStorage<T> {

  T getNode(String nodeId);

  Map<String, T> getMap();
}
