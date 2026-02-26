package com.github.jawisimo.botengine.interaction.content;

import com.github.jawisimo.botengine.interaction.content.handler.ContentHandler;
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
public class ContentExecutor {

  private final List<ContentHandler> contentHandlers;
  private final MessageRepository messageRepository;
  private final DialogValidator dialogValidator;

  public void execute(DialogNode node, String chatId) {
    if (node.content() == null) return;

    for (ContentNode contentNode : node.content()) {
      dialogValidator.validateContent(contentNode, contentHandlers);
      for (ContentHandler handler : contentHandlers) {
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
