package com.jawisimo.tbcfstarter.service;

import com.jawisimo.tbcfstarter.handler.NodeHandler;
import com.jawisimo.tbcfstarter.model.Button;
import com.jawisimo.tbcfstarter.model.ButtonType;
import com.jawisimo.tbcfstarter.model.DialogNode;
import com.jawisimo.tbcfstarter.repository.DialogRepository;
import com.jawisimo.tbcfstarter.service.command.CommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateService {

    private final NodeHandler nodeHandler;
    private final DialogRepository dialogRepository;
    private final MessageCleanupService cleanupService;
    private final UserStateService userStateService;
    private final List<CommandService> commandServices;

    @Async("asyncBotExecutor")
    public void onUpdateReceived(Update update) {
        String chatId = extractChatId(update);

        if (chatId == null) {
            log.warn("Unsupported update type: {}", update);
            return;
        }

        String userInput = extractUserInput(update);

        cleanupService.deleteRedundantMessage(update.getMessage());

        if (handleCommandIfExists(chatId, update, userInput)) {
            return;
        }

        userInput = resolveNextFromReplyButtons(chatId, userInput);
        processNode(chatId, userInput);
    }

    private String extractChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId().toString();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId().toString();
        }
        return null;
    }

    private String extractUserInput(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getText();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getData();
        }
        return null;
    }

    private boolean handleCommandIfExists(String chatId, Update update, String userInput) {
        if (userInput == null) return false;

        for (CommandService commandService : commandServices) {
            if (userInput.equals(commandService.getCommandKey())) {
                commandService.executeCommand(chatId, update);
                return true;
            }
        }
        return false;
    }

    private String resolveNextFromReplyButtons(String chatId, String userInput) {
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
            log.warn("User input is null, skipping node processing for chatId={}", chatId);
            return;
        }

        DialogNode nextNode = dialogRepository.getDialogNode(userInput);
        if (nextNode == null) {
            log.warn("Could not find node for input='{}'", userInput);
            return;
        }

        nodeHandler.handle(nextNode, chatId);
        userStateService.saveUserState(chatId, userInput);
    }
}
