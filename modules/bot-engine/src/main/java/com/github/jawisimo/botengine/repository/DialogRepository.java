package com.github.jawisimo.botengine.repository;

import com.github.jawisimo.botengine.interaction.node.model.DialogNode;

public interface DialogRepository {

  DialogNode getNode(String nodeId);
}
