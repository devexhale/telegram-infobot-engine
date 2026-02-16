package com.jawisimo.tbcfstarter.interaction.media;

import com.jawisimo.tbcfstarter.interaction.media.handler.MediaHandler;
import com.jawisimo.tbcfstarter.interaction.node.model.ContentNode;
import com.jawisimo.tbcfstarter.interaction.node.model.DialogNode;
import com.jawisimo.tbcfstarter.repository.MessageRepository;
import com.jawisimo.tbcfstarter.validator.DialogValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MediaExecutor {
  private final List<MediaHandler> mediaHandlers;
  private final MessageRepository messageRepository;
  private final DialogValidator dialogValidator;

  public void execute(DialogNode node, String chatId) {
    if (node.content() == null) return;

    for (ContentNode contentNode : node.content()) {
      dialogValidator.validateContent(contentNode, mediaHandlers);
      for (MediaHandler handler : mediaHandlers) {
        if (handler.canHandle(contentNode)) {
          Message sent = handler.handle(contentNode, chatId);
          if (sent != null) {
            messageRepository.save(chatId, sent.getMessageId());
          }
        }
      }
    }
  }
}
