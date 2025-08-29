package com.jawisimo.tbcfstarter.service;

import com.jawisimo.tbcfstarter.handler.NodeHandler;
import com.jawisimo.tbcfstarter.model.Button;
import com.jawisimo.tbcfstarter.model.ButtonType;
import com.jawisimo.tbcfstarter.model.DialogNode;
import com.jawisimo.tbcfstarter.repository.DialogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateService {
    private final NodeHandler nodeHandler;
    private final DialogRepository dialogRepository;

    private static final String START_NODE_KEY = "/start";

    @Async("asyncBotExecutor")
    public void onUpdateReceived(Update update) {
        String chatId;
        String userInput;

        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId().toString();
            userInput = update.getMessage().getText();

            // Для reply-кнопок знаходимо next по label
            DialogNode startNode = dialogRepository.getDialogNode(START_NODE_KEY);
            if (startNode != null && startNode.buttonType() == ButtonType.REPLY) {
                String finalUserInput = userInput;
                String next = startNode.buttons().stream()
                        .filter(b -> b.getLabel().equals(finalUserInput))
                        .map(Button::getNext)
                        .findFirst()
                        .orElse(null);

                if (next != null) {
                    userInput = next;
                }
            }

        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId().toString();
            userInput = update.getCallbackQuery().getData();
        } else {
            log.warn("Unsupported update type: {}", update);
            return;
        }

        DialogNode nextNode = dialogRepository.getDialogNode(userInput);

        if (nextNode == null) {
            log.warn("Could not find node for input='{}'", userInput);
            return;
        }

        nodeHandler.handle(nextNode, chatId);
    }
}
