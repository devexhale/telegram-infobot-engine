package com.jawisimo.tbcfstarter.repository;

import com.jawisimo.tbcfstarter.dialog.node.model.DialogNode;

public interface DialogRepository {
    DialogNode getDialogNode(String nodeId);
}
