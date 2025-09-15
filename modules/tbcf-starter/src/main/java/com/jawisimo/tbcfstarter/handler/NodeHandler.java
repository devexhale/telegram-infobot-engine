package com.jawisimo.tbcfstarter.handler;

import com.jawisimo.tbcfstarter.model.ContentNode;
import com.jawisimo.tbcfstarter.model.ContentType;
import com.jawisimo.tbcfstarter.model.DialogNode;
import com.jawisimo.tbcfstarter.repository.DialogRepository;
import com.jawisimo.tbcfstarter.service.MessageCleanupService;
import com.jawisimo.tbcfstarter.service.state.UserStateService;
import com.jawisimo.tbcfstarter.validator.DialogValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class NodeHandler {
    private final List<ContentHandler> contentHandlers;
    private final KeyboardMarkupHandler keyboardHandler;
    private final MessageCleanupService cleanupService;
    private final DialogValidator dialogValidator;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private final UserStateService userStateService;

    private final DialogRepository dialogRepository;
    private final ConcurrentHashMap<String, Object> chatLocks = new ConcurrentHashMap<>();
    private final TelegramClient client;

    private static final String UPLOADING_MESSAGE = "Uploading media. Wait...";

    public void handle(String chatId, String userInput) {
        if (userInput == null) {
            log.warn("User input is null. Message deleted from chatId={}", chatId);
            return;
        }

        DialogNode nextNode = dialogRepository.getDialogNode(userInput);
        if (nextNode != null) {
            handle(nextNode, chatId);
            userStateService.saveUserState(chatId, userInput);
        } else {
            log.warn("No dialog node found for input='{}'. Message deleted from chatId={}", userInput, chatId);
        }
    }

    public void handle(DialogNode dialogNode, String chatId) {
        Object lock = chatLocks.computeIfAbsent(chatId, k -> new Object());
        synchronized (lock) {
            cleanupService.clearLastNode(chatId);
            processContent(dialogNode, chatId);
            processKeyboard(dialogNode, chatId);
        }
    }

    private void processContent(DialogNode dialogNode, String chatId) {
        if (dialogNode.content() == null) return;

        for (ContentNode contentNode : dialogNode.content()) {
            dialogValidator.validateContentNode(contentNode, contentHandlers);
            handleContentNode(contentNode, chatId);
        }
    }

    private void handleContentNode(ContentNode contentNode, String chatId) {
        for (ContentHandler handler : contentHandlers) {
            if (!handler.supports(contentNode)) continue;

            Message tempMsg = null;
            if (isSlowMedia(contentNode)) {
                tempMsg = sendTempMessage(chatId);
            }

            Message sent = handler.handle(contentNode, chatId);
            if (sent != null) {
                cleanupService.registerMessage(chatId, sent.getMessageId());
            }

            if (tempMsg != null) {
                cleanupService.deleteMessage(chatId, tempMsg.getMessageId());
            }
        }
    }

    private void processKeyboard(DialogNode dialogNode, String chatId) {
        dialogValidator.validateButtons(dialogNode);
        Message keyboardMsg = keyboardHandler.handle(dialogNode, chatId);
        if (keyboardMsg != null) {
            cleanupService.registerMessage(chatId, keyboardMsg.getMessageId());
        }
    }

    private boolean isSlowMedia(ContentNode node) {
        return node.getType() == ContentType.MEDIA && node.getMedia() != null;
    }

    private Message sendTempMessage(String chatId) {
        try {
            Message msg = client.execute(
                    SendMessage.builder()
                            .chatId(chatId)
                            .text(UPLOADING_MESSAGE)
                            .build()
            );
            cleanupService.registerMessage(chatId, msg.getMessageId());
            return msg;
        } catch (TelegramApiException e) {
            log.error("Failed to send temp message: {}", e.getMessage(), e);
            return null;
        }
    }

}
