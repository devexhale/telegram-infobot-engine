package com.jawisimo.tbcfstarter.service;

import com.jawisimo.tbcfstarter.handler.NodeHandler;
import com.jawisimo.tbcfstarter.model.Button;
import com.jawisimo.tbcfstarter.model.ButtonType;
import com.jawisimo.tbcfstarter.model.DialogNode;
import com.jawisimo.tbcfstarter.repository.DialogRepository;
import com.jawisimo.tbcfstarter.service.command.CommandService;
import com.jawisimo.tbcfstarter.service.state.UserStateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateService {

    private final NodeHandler nodeHandler;
    private final DialogRepository dialogRepository;
    private final MessageCleanupService cleanupService;
    private final List<CommandService> commandServices;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private final UserStateService userStateService;

    @Async("asyncBotVirtualExecutor")
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            executeMessage(update);
        } else if (update.hasCallbackQuery()) {
            executeCallback(update);
        } else {
            log.warn("Unsupported update type: {}", update);
        }
    }

    private void executeMessage(Update update) {
        Message message = update.getMessage();
        String chatId = message.getChatId().toString();
        String userInput = message.getText();
        cleanupService.deleteRedundantMessage(message);
        if (executeCommandIfExists(chatId, userInput)) return;
        userInput = getNextNodeKeyFromReply(chatId, userInput);
        processNode(chatId, userInput);
    }

    private void executeCallback(Update update) {
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        String userInput = update.getCallbackQuery().getData();
        if (executeCommandIfExists(chatId, userInput)) return;
        processNode(chatId, userInput);
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

    private String getNextNodeKeyFromReply(String chatId, String userInput) {
        String currentNodeKey = userStateService.getUserStateOrDefault(chatId, commandServices.getFirst().getCommandKey());
        DialogNode currentNode = dialogRepository.getDialogNode(currentNodeKey);

        if (currentNode != null && currentNode.buttonType() == ButtonType.REPLY && currentNode.buttons() != null) {
            return currentNode.buttons().stream()
                    .filter(b -> b.getLabel().equals(userInput))
                    .map(Button::getNext)
                    .findFirst()
                    .orElse(userInput);
        }

        return userInput;
    }

    private void processNode(String chatId, String userInput) {
        if (userInput == null) {
            log.warn("User input is null, skipping node processing. Message deleted from chat={}", chatId);
            return;
        }

        DialogNode nextNode = dialogRepository.getDialogNode(userInput);
        if (nextNode == null) {
            log.warn("Could not find node for input='{}'. Message deleted from chat={}", userInput, chatId);
            return;
        }

        nodeHandler.handle(nextNode, chatId);
        userStateService.saveUserState(chatId, userInput);
    }

}
