package com.jawisimo.tbcfstarter.handler.command;

import com.jawisimo.tbcfstarter.command.Command;
import com.jawisimo.tbcfstarter.handler.NodeProcessor;
import com.jawisimo.tbcfstarter.model.DialogNode;
import com.jawisimo.tbcfstarter.repository.CaffeineMessageRepository;
import com.jawisimo.tbcfstarter.service.UserStateService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)
public abstract class AbstractCommandHandler implements CommandHandler {
    private final Command command;
    private final CaffeineMessageRepository caffeineMessageRepository;
    private final UserStateService userStateService;
    private final NodeProcessor nodeProcessor;

    @Override
    public void handle(String chatId) {
        String nodeKey = resolveNodeKey(chatId);
        DialogNode node = caffeineMessageRepository.getDialogNode(nodeKey);

        if (node == null) {
            log.warn("Node '{}' not found for chat {}", nodeKey, chatId);
            return;
        }

        nodeProcessor.processNode(node, chatId);
        userStateService.saveUserStateIfPersist(chatId, nodeKey);
    }

    @Override
    public String getCommandKey() {
        return getCommand().getCommandName();
    }

    protected abstract String resolveNodeKey(String chatId);
}
