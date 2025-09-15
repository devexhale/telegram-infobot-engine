package com.jawisimo.tbcfstarter.service.command;

import com.jawisimo.tbcfstarter.command.StartCommand;
import com.jawisimo.tbcfstarter.model.DialogNode;
import com.jawisimo.tbcfstarter.repository.DialogRepository;
import com.jawisimo.tbcfstarter.handler.NodeProcessor;
import com.jawisimo.tbcfstarter.service.state.UserStateService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)
public abstract class AbstractNodeCommandService implements CommandService {
    private final StartCommand startCommand;
    private final DialogRepository dialogRepository;
    private final UserStateService userStateService;
    private final NodeProcessor nodeProcessor;

    @Override
    public void execute(String chatId) {
        String nodeKey = resolveNodeKey(chatId);
        DialogNode node = dialogRepository.getDialogNode(nodeKey);

        if (node == null) {
            log.warn("Node '{}' not found for chat {}", nodeKey, chatId);
            return;
        }

        nodeProcessor.processNode(node, chatId);
        userStateService.saveUserState(chatId, nodeKey);
    }

    protected abstract String resolveNodeKey(String chatId);

}
