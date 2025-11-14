package com.jawisimo.tbcfstarter.handler.command;

import com.jawisimo.tbcfstarter.handler.NodeProcessor;
import com.jawisimo.tbcfstarter.model.DialogNode;
import com.jawisimo.tbcfstarter.repository.DialogRepository;
import com.jawisimo.tbcfstarter.service.UserStateService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)
@Slf4j
abstract class AbstractCommandHandler implements CommandHandler{
    private final DialogRepository dialogRepository;
    private final UserStateService userStateService;
    private final NodeProcessor nodeProcessor;

    @Override
    public void handle(String chatId) {
        DialogNode node = dialogRepository.getDialogNode(getNodeKey(chatId));

        if (node == null) {
            log.warn("Node '{}' not found for chat {}", getNodeKey(chatId), chatId);
            return;
        }

        nodeProcessor.processNode(node, chatId);
        userStateService.saveUserStateIfPersist(chatId, getNodeKey(chatId));
    }

    abstract String getNodeKey(String chatId);
}
