package com.jawisimo.tbcfstarter.repository;

import com.jawisimo.tbcfstarter.interaction.node.model.DialogNode;

public interface DialogRepository {
    DialogNode getNode(String nodeId);
}
