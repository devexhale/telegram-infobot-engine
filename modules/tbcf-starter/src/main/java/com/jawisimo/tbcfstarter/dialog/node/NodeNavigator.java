package com.jawisimo.tbcfstarter.dialog.node;

import com.jawisimo.tbcfstarter.dialog.command.commandset.StartCommand;
import com.jawisimo.tbcfstarter.dialog.node.model.Button;
import com.jawisimo.tbcfstarter.dialog.node.model.ButtonType;
import com.jawisimo.tbcfstarter.dialog.node.model.DialogNode;
import com.jawisimo.tbcfstarter.repository.DialogRepository;
import com.jawisimo.tbcfstarter.service.UserStateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NodeNavigator {
    private final UserStateService userStateService;
    private final DialogRepository dialogRepository;
    private final NodeExecutor nodeExecutor;

    public String getNextNodeKey(String chatId, String userInput) {
        DialogNode currentNode = getCurrentNode(chatId);

        if (currentNode != null
                && currentNode.buttonType() == ButtonType.REPLY
                && currentNode.buttons() != null) {

            return currentNode.buttons().stream()
                    .filter(btn -> btn.getLabel().equals(userInput))
                    .map(Button::getNext)
                    .findFirst()
                    .orElse(userInput);
        }

        return userInput;
    }

    public boolean navigateToNode(String chatId, String nodeKey) {
        if (nodeKey == null) {
            return false;
        }

        DialogNode node = dialogRepository.getDialogNode(nodeKey);

        if (node == null) {
            return false;
        }

        nodeExecutor.execute(node, chatId);
        return true;
    }

    private DialogNode getCurrentNode(String chatId) {
        String currentNodeKey =
                userStateService.getUserStateOrDefault(chatId, StartCommand.COMMAND_NAME);

        return dialogRepository.getDialogNode(currentNodeKey);
    }
}
