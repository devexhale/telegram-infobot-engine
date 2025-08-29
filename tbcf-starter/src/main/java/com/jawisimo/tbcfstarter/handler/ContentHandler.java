package com.jawisimo.tbcfstarter.handler;

import com.jawisimo.tbcfstarter.model.ContentNode;

public interface ContentHandler {
    boolean supports(ContentNode contentNode);
    void handle(ContentNode contentNode, String chatId);
}
