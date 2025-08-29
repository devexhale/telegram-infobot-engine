package com.jawisimo.tbcfstarter.handler;

import com.jawisimo.tbcfstarter.model.Button;
import com.jawisimo.tbcfstarter.model.ButtonType;
import com.jawisimo.tbcfstarter.model.DialogNode;
import com.jawisimo.tbcfstarter.repository.DialogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class NavigationHandler {

    private final DialogRepository dialogRepository;

    /**
     * Повертає ноду, на яку потрібно перейти в залежності від типу кнопки та userInput.
     *
     * @param currentNode Поточна нода, де була натиснута кнопка
     * @param userInput   Текст або callbackData від користувача
     * @return наступна DialogNode, або null якщо не знайдено
     */
    public DialogNode navigate(DialogNode currentNode, String userInput) {
        if (currentNode == null) {
            log.warn("Current node is null");
            return null;
        }

        // Якщо кнопки reply, шукаємо по label
        if (currentNode.buttonType() == ButtonType.REPLY) {
            return currentNode.buttons().stream()
                    .filter(b -> b.getLabel().equals(userInput))
                    .map(Button::getNext)
                    .map(dialogRepository::getDialogNode)
                    .findFirst()
                    .orElse(null);
        }

        // Inline кнопки — userInput вже містить next
        if (currentNode.buttonType() == ButtonType.INLINE) {
            return dialogRepository.getDialogNode(userInput);
        }

        // Якщо немає кнопок або невідомий тип
        log.warn("Unknown button type or no buttons for node '{}'", currentNode);
        return null;
    }
}
