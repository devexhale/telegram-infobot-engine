package com.github.jawisimo.botengine.interaction.node.model;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public final class DialogMap {

  private Map<String, DialogNode> nodes;

  public DialogNode getNode(String nodeId) {
    return nodes.get(nodeId);
  }

  public boolean containsNodeKey(String key) {
    return nodes.containsKey(key);
  }
}
