package com.jawisimo.tbcfstarter.interaction.command.handler;

import com.jawisimo.tbcfstarter.interaction.node.NodeExecutor;
import com.jawisimo.tbcfstarter.interaction.node.model.DialogNode;
import com.jawisimo.tbcfstarter.repository.DialogRepository;
import com.jawisimo.tbcfstarter.service.UserStateService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Getter(AccessLevel.PACKAGE)
@Slf4j
public abstract class AbstractCommandHandler implements CommandHandler {
    private final DialogRepository dialogRepository;
    private final UserStateService userStateService;
    private final NodeExecutor nodeExecutor;

    @Override
    public void handle(String chatId) {
        DialogNode node = dialogRepository.getNode(getNodeKey(chatId));

        if (node == null) {
            log.warn("Node '{}' not found for chat {}", getNodeKey(chatId), chatId);
            return;
        }

        nodeExecutor.execute(node, chatId);
        userStateService.saveUserStateIfPersist(chatId, getNodeKey(chatId));
    }

    abstract String getNodeKey(String chatId);
}
