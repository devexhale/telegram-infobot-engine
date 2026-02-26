package com.github.jawisimo.botengine.interaction.media.handler;

import com.github.jawisimo.botengine.interaction.node.model.ContentNode;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface MediaHandler {

  boolean canHandle(ContentNode contentNode);

  Message handle(ContentNode contentNode, String chatId);
}
