package com.github.jawisimo.tbcfstarter.interaction.media.handler;

import com.github.jawisimo.tbcfstarter.interaction.node.model.ContentNode;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface MediaHandler {

  boolean canHandle(ContentNode contentNode);

  Message handle(ContentNode contentNode, String chatId);
}
