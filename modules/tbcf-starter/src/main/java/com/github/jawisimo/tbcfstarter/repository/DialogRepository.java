package com.github.jawisimo.tbcfstarter.repository;

import com.github.jawisimo.tbcfstarter.interaction.node.model.DialogNode;

public interface DialogRepository {

  DialogNode getNode(String nodeId);
}
