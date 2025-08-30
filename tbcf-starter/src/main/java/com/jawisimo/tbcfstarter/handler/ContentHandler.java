package com.jawisimo.tbcfstarter.handler;

import com.jawisimo.tbcfstarter.model.ContentNode;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface ContentHandler {
    boolean supports(ContentNode contentNode);
    Message handle(ContentNode contentNode, String chatId);
}
