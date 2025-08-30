package com.jawisimo.tbcfstarter.service.command;

import com.jawisimo.tbcfstarter.command.LastCommand;
import com.jawisimo.tbcfstarter.command.StartCommand;
import com.jawisimo.tbcfstarter.handler.NodeHandler;
import com.jawisimo.tbcfstarter.model.DialogNode;
import com.jawisimo.tbcfstarter.repository.DialogRepository;
import com.jawisimo.tbcfstarter.service.UserStateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
@Slf4j
public class LastCommandService implements CommandService {
    private final StartCommand startCommand;
    private final LastCommand lastCommand;
    private final NodeHandler nodeHandler;
    private final DialogRepository dialogRepository;
    private final UserStateService userStateService;

    @Override
    public String getCommandKey() {
        return lastCommand.getCommandName();
    }

    @Override
    public void execute(String chatId) {
        // Отримуємо попередню ноду користувача
        String previousNodeKey = userStateService.getUserStateOrDefault(chatId, startCommand.getCommandName());

        DialogNode previousNode = dialogRepository.getDialogNode(previousNodeKey);
        if (previousNode == null) {
            log.warn("Previous node '{}' not found for chat {}", previousNodeKey, chatId);
            return;
        }

        // Відправляємо попередню ноду
        nodeHandler.handle(previousNode, chatId);

        // Оновлюємо стан користувача на попередню ноду
        userStateService.saveUserState(chatId, previousNodeKey);
    }

}
