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
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateService {

    private final NodeHandler nodeHandler;
    private final DialogRepository dialogRepository;
    private final MessageCleanupService cleanupService;
    private final UserStateService userStateService;

    private static final String START_NODE_KEY = "/start";
    private static final String LAST_NODE_KEY = "/last";

    @Async("asyncBotExecutor")
    public void onUpdateReceived(Update update) {
        String chatId;
        String userInput;

        if (update.hasMessage()) {
            Message userMessage = update.getMessage();
            chatId = userMessage.getChatId().toString();
            userInput = userMessage.getText();

            // Завжди видаляємо вхідне повідомлення користувача
            cleanupService.deleteRedundantMessages(userMessage);

            // Якщо користувач ввів /start — завжди стартова нода
            if (START_NODE_KEY.equals(userInput)) {
                userInput = START_NODE_KEY;
            } else if (LAST_NODE_KEY.equals(userInput)) {
                // Відновлення попередньої ноди
                userInput = userStateService.getUserStateOrDefault(chatId, START_NODE_KEY);
            } else {
                // Завантажуємо поточну ноду з Redis
                String currentNodeKey = userStateService.getUserStateOrDefault(chatId, START_NODE_KEY);
                DialogNode currentNode = dialogRepository.getDialogNode(currentNodeKey);

                // Якщо кнопки reply → шукаємо відповідну next по label
                if (currentNode != null && currentNode.buttonType() == ButtonType.REPLY && currentNode.buttons() != null) {
                    String finalUserInput = userInput;
                    userInput = currentNode.buttons().stream()
                            .filter(b -> b.getLabel().equals(finalUserInput))
                            .map(Button::getNext)
                            .findFirst()
                            .orElse(userInput); // fallback — залишаємо текст як є
                }
            }

        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId().toString();
            // callback data вже містить next
            userInput = update.getCallbackQuery().getData();
        } else {
            log.warn("Unsupported update type: {}", update);
            return;
        }

        // Отримуємо наступну ноду
        DialogNode nextNode = dialogRepository.getDialogNode(userInput);
        if (nextNode == null) {
            log.warn("Could not find node for input='{}'", userInput);
            return;
        }

        // Відправляємо ноду та зберігаємо стан користувача
        nodeHandler.handle(nextNode, chatId);
        userStateService.saveUserState(chatId, userInput);
    }
}
