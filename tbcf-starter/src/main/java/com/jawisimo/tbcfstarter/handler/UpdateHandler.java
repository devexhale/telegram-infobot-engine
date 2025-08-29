package com.jawisimo.tbcfstarter.handler;

import com.jawisimo.tbcfstarter.model.DialogNode;

public interface UpdateHandler {
    boolean supports(DialogNode node);
    void handle(DialogNode node, String chatId);
}
