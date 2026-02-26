package com.github.jawisimo.botengine.interaction.media;

import com.github.jawisimo.botengine.interaction.media.handler.MediaHandler;
import com.github.jawisimo.botengine.interaction.node.model.ContentNode;
import com.github.jawisimo.botengine.interaction.node.model.DialogNode;
import com.github.jawisimo.botengine.repository.MessageRepository;
import com.github.jawisimo.botengine.validator.DialogValidator;
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
