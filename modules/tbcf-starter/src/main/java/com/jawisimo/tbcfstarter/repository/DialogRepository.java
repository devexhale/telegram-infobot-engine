package com.jawisimo.tbcfstarter.repository;

import com.jawisimo.tbcfstarter.model.DialogNode;

public interface DialogRepository {
    DialogNode getDialogNode(String nodeId);
}
