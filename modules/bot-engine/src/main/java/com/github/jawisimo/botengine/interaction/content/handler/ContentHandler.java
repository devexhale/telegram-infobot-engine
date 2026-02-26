package com.github.jawisimo.botengine.interaction.content.handler;

import com.github.jawisimo.botengine.interaction.node.model.ContentNode;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface ContentHandler {

  boolean canHandle(ContentNode contentNode);

  Message handle(ContentNode contentNode, String chatId);
}
