package com.jawisimo.tbcfstarter.content;

import com.jawisimo.tbcfstarter.content.handler.ContentHandler;
import com.jawisimo.tbcfstarter.model.ContentNode;
import com.jawisimo.tbcfstarter.model.DialogNode;
import com.jawisimo.tbcfstarter.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ContentExecutor {
    private final List<ContentHandler> contentHandlers;
    private final MessageRepository messageRepository;

    public void execute(DialogNode node, String chatId) {
        if (node.content() == null) return;

        for (ContentNode contentNode : node.content()) {
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
