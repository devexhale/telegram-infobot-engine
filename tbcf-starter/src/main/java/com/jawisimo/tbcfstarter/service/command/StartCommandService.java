package com.jawisimo.tbcfstarter.service.command;

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
public class StartCommandService implements CommandService {

    private final StartCommand startCommand;
    private final NodeHandler nodeHandler;
    private final DialogRepository dialogRepository;
    private final UserStateService userStateService;

    @Override
    public String getCommandKey() {
        return startCommand.getCommandName();
    }

    @Override
    public void executeCommand(String chatId, Update update) {
        // Завжди стартова нода
        DialogNode startNode = dialogRepository.getDialogNode(getCommandKey());
        if (startNode == null) {
            log.error("Start node not found in repository!");
            return;
        }

        // Відправляємо стартову ноду
        nodeHandler.handle(startNode, chatId);

        // Зберігаємо стан користувача
        userStateService.saveUserState(chatId, getCommandKey());
    }
}
