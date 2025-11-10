package com.jawisimo.tbcfstarter.handler;

import com.jawisimo.tbcfstarter.command.StartCommand;
import com.jawisimo.tbcfstarter.model.Button;
import com.jawisimo.tbcfstarter.model.ButtonType;
import com.jawisimo.tbcfstarter.model.DialogNode;
import com.jawisimo.tbcfstarter.repository.CaffeineMessageRepository;
import com.jawisimo.tbcfstarter.service.MessageCleanupService;
import com.jawisimo.tbcfstarter.service.UserStateService;
import com.jawisimo.tbcfstarter.handler.command.CommandHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DialogHandler {
    private final MessageCleanupService cleanupService;
    private final UserStateService userStateService;
    private final CaffeineMessageRepository caffeineMessageRepository;
    private final NodeProcessor nodeProcessor;
    private final List<CommandHandler> commandServices;

    public void handleMessage(Message message) {
        cleanupService.deleteRedundantMessage(message);
        String chatId = message.getChatId().toString();
        String userInput = message.getText();
        if (handleCommandIfExists(chatId, userInput)) return;
        String nextNodeKey = resolveNextNodeKey(chatId, userInput);
        DialogNode node = caffeineMessageRepository.getDialogNode(nextNodeKey);

        if (node != null) {
            nodeProcessor.processNode(node, chatId);
            userStateService.saveUserStateIfPersist(chatId, nextNodeKey);
        } else {
            log.warn("Irrelevant message sent: \"{}\". Message deleted from chat: {}", nextNodeKey, chatId);
        }
    }

    public void handleCallback(CallbackQuery callbackQuery) {
        String chatId = callbackQuery.getMessage().getChatId().toString();
        String callbackData = callbackQuery.getData();
        cleanupService.clearLastNode(chatId);
        if (handleCommandIfExists(chatId, callbackData)) return;
        handleNodeByKey(chatId, callbackData);
        userStateService.saveUserStateIfPersist(chatId, callbackData);
    }


    private void handleNodeByKey(String chatId, String nodeKey) {
        if (nodeKey == null) {
            log.warn("User input is null. Message deleted from chat: {}", chatId);
            return;
        }

        DialogNode node = caffeineMessageRepository.getDialogNode(nodeKey);

        if (node != null) {
            nodeProcessor.processNode(node, chatId);
        } else {
            log.warn("No dialog node found for input: {}. Message deleted from chat: {}", nodeKey, chatId);
        }
    }

    // ================== HELPERS ==================

    private String resolveNextNodeKey(String chatId, String userInput) {
        DialogNode currentNode = getCurrentNode(chatId);

        if (currentNode != null
                && currentNode.buttonType() == ButtonType.REPLY
                && currentNode.buttons() != null) {
            return currentNode.buttons().stream()
                    .filter(b -> b.getLabel().equals(userInput))
                    .map(Button::getNext)
                    .findFirst()
                    .orElse(userInput);
        }

        return userInput;
    }

    private DialogNode getCurrentNode(String chatId) {
        String currentNodeKey = userStateService.getUserStateOrDefault(
                chatId,
                StartCommand.COMMAND_NAME
        );

        return caffeineMessageRepository.getDialogNode(currentNodeKey);
    }

    private boolean handleCommandIfExists(String chatId, String userInput) {
        if (userInput == null) return false;

        for (CommandHandler commandService : commandServices) {
            if (userInput.equals(commandService.getCommandKey())) {
                commandService.handle(chatId);
                return true;
            }
        }

        return false;
    }
}
