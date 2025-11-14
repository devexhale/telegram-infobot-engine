package com.jawisimo.tbcfstarter.handler.content;

import com.jawisimo.tbcfstarter.model.ContentNode;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface ContentHandler {

    boolean canHandle(ContentNode contentNode);

    Message handle(ContentNode contentNode, String chatId);
}
