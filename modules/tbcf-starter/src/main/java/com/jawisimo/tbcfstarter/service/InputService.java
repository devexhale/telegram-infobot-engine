package com.jawisimo.tbcfstarter.service;

import com.jawisimo.tbcfstarter.handler.NodeHandler;
import com.jawisimo.tbcfstarter.model.Button;
import com.jawisimo.tbcfstarter.model.ButtonType;
import com.jawisimo.tbcfstarter.model.DialogNode;
import com.jawisimo.tbcfstarter.repository.DialogRepository;
import com.jawisimo.tbcfstarter.service.command.CommandService;
import com.jawisimo.tbcfstarter.service.state.UserStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InputService {

    private final List<CommandService> commandServices;
    private final DialogRepository dialogRepository;
    private final NodeHandler nodeHandler;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private final UserStateService userStateService;

    private final MessageCleanupService cleanupService;

    public void executeMessage(Message message) {
        cleanupService.deleteRedundantMessage(message);
        String chatId = message.getChatId().toString();
        String userInput = message.getText();
        if (executeCommandIfExists(chatId, userInput)) return;
        userInput = getNextNodeKeyFromReply(chatId, userInput);
        nodeHandler.handle(chatId, userInput);
    }

    public void executeCallback(CallbackQuery callbackQuery) {
        String chatId = callbackQuery.getMessage().getChatId().toString();
        String callbackData = callbackQuery.getData();

        if (executeCommandIfExists(chatId, callbackData)) return;

        DialogNode nextNode = dialogRepository.getDialogNode(callbackData);

        if (nextNode != null) {
            nodeHandler.handle(nextNode, chatId);
            userStateService.saveUserState(chatId, callbackData);
        }
    }

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
