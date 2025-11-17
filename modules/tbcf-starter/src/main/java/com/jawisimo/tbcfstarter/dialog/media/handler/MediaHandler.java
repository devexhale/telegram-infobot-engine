package com.jawisimo.tbcfstarter.dialog.media.handler;

import com.jawisimo.tbcfstarter.dialog.node.model.ContentNode;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface MediaHandler {

    boolean canHandle(ContentNode contentNode);

    Message handle(ContentNode contentNode, String chatId);
}
