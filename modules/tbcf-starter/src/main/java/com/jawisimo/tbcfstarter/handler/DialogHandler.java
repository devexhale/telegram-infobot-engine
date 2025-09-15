package com.jawisimo.tbcfstarter.handler;

import com.jawisimo.tbcfstarter.model.Button;
import com.jawisimo.tbcfstarter.model.ButtonType;
import com.jawisimo.tbcfstarter.model.DialogNode;
import com.jawisimo.tbcfstarter.repository.DialogRepository;
import com.jawisimo.tbcfstarter.service.MessageCleanupService;
import com.jawisimo.tbcfstarter.service.command.CommandService;
import com.jawisimo.tbcfstarter.service.state.UserStateService;
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
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private final UserStateService userStateService;
    private final DialogRepository dialogRepository;
    private final NodeProcessor nodeProcessor;
    private final List<CommandService> commandServices;

    // ================== PUBLIC HANDLERS ==================

    public void handleMessage(Message message) {
        cleanupService.deleteRedundantMessage(message);
        String chatId = message.getChatId().toString();
        String userInput = message.getText();

        if (executeCommandIfExists(chatId, userInput)) return;

        userInput = getNextNodeKeyFromReply(chatId, userInput);
        handleNodeByKey(chatId, userInput);
    }

    public void handleCallback(CallbackQuery callbackQuery) {
        String chatId = callbackQuery.getMessage().getChatId().toString();
        String callbackData = callbackQuery.getData();

        cleanupService.clearLastNode(chatId);

        if (executeCommandIfExists(chatId, callbackData)) return;

        handleNodeByKey(chatId, callbackData);
        userStateService.saveUserState(chatId, callbackData);
    }

    // ================== INTERNAL NODE HANDLING ==================

    private void handleNodeByKey(String chatId, String nodeKey) {
        if (nodeKey == null) {
            log.warn("User input is null. Message deleted from chatId={}", chatId);
            return;
        }

        DialogNode node = dialogRepository.getDialogNode(nodeKey);
        if (node != null) {
            nodeProcessor.processNode(node, chatId);
            userStateService.saveUserState(chatId, nodeKey);
        } else {
            log.warn("No dialog node found for input='{}'. Message deleted from chatId={}", nodeKey, chatId);
        }
    }

    // ================== HELPERS ==================

    private String getNextNodeKeyFromReply(String chatId, String userInput) {
        DialogNode currentNode = getCurrentNode(chatId);

        if (currentNode != null && currentNode.buttonType() == ButtonType.REPLY && currentNode.buttons() != null) {
            return currentNode.buttons().stream()
                    .filter(b -> b.getLabel().equals(userInput))
                    .map(Button::getNext)
                    .findFirst()
                    .orElse(userInput);
        }

        return userInput;
    }

    private DialogNode getCurrentNode(String chatId) {
        String currentNodeKey = userStateService.getUserStateOrDefault(chatId, commandServices.getFirst().getCommandKey());
        return dialogRepository.getDialogNode(currentNodeKey);
    }

    private boolean executeCommandIfExists(String chatId, String userInput) {
        if (userInput == null) return false;

        for (CommandService commandService : commandServices) {
            if (userInput.equals(commandService.getCommandKey())) {
                commandService.execute(chatId);
                return true;
            }
        }
        return false;
    }

}
