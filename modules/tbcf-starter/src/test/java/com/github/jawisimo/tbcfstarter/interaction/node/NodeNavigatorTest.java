package com.github.jawisimo.tbcfstarter.interaction.node;

import com.github.jawisimo.tbcfstarter.interaction.command.commandset.StartCommand;
import com.github.jawisimo.tbcfstarter.interaction.node.model.Button;
import com.github.jawisimo.tbcfstarter.interaction.node.model.ButtonType;
import com.github.jawisimo.tbcfstarter.interaction.node.model.DialogNode;
import com.github.jawisimo.tbcfstarter.repository.DialogRepository;
import com.github.jawisimo.tbcfstarter.service.UserStateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NodeNavigatorTest {

  private static final String CHAT_ID = "123456789";
  private static final String USER_INPUT = "some input";
  private static final String CURRENT_NODE_KEY = "current-node";
  private static final String NODE_MESSAGE = "Test message";

  private static final String BTN_LABEL = "btn";
  private static final String BTN_NEXT = "next";
  private static final String BTN_URL = "https://example.com";

  @Mock private UserStateService userStateService;
  @Mock private DialogRepository dialogRepository;
  @Mock private NodeExecutor nodeExecutor;

  @InjectMocks private NodeNavigator nodeNavigator;

  @BeforeEach
  void setUp() {
    doReturn(CURRENT_NODE_KEY)
        .when(userStateService)
        .getUserStateOrDefault(CHAT_ID, StartCommand.COMMAND_NAME);
  }

  @Test
  void getNextNodeKey_shouldReturnUserInput_whenCurrentNodeIsNull() {
    doReturn(null).when(dialogRepository).getNode(CURRENT_NODE_KEY);

    String result = nodeNavigator.getNextNodeKey(CHAT_ID, USER_INPUT);

    assertEquals(USER_INPUT, result);
  }

  @Test
  void getNextNodeKey_shouldReturnUserInput_whenCurrentNodeIsNotReplyType() {
    Button button = new Button(BTN_LABEL, BTN_NEXT, null);
    DialogNode currentNode = new DialogNode(null, NODE_MESSAGE, ButtonType.INLINE, List.of(button));

    doReturn(currentNode).when(dialogRepository).getNode(CURRENT_NODE_KEY);

    String result = nodeNavigator.getNextNodeKey(CHAT_ID, USER_INPUT);

    assertEquals(USER_INPUT, result);
  }

  @Test
  void getNextNodeKey_shouldReturnUserInput_whenCurrentNodeIsReplyAndButtonsAreNull() {
    Button button = new Button(BTN_LABEL, BTN_NEXT, null);
    DialogNode currentNode = new DialogNode(null, NODE_MESSAGE, ButtonType.REPLY, List.of(button));
    DialogNode spyNode = spy(currentNode);

    doReturn(null).when(spyNode).buttons();
    doReturn(spyNode).when(dialogRepository).getNode(CURRENT_NODE_KEY);

    String result = nodeNavigator.getNextNodeKey(CHAT_ID, USER_INPUT);

    assertEquals(USER_INPUT, result);
  }

  @Test
  void getNextNodeKey_shouldReturnNextNodeKey_whenCurrentNodeIsReplyAndButtonMatched() {
    Button matched = new Button(BTN_LABEL, BTN_NEXT, null);
    Button other = new Button("other", "other-next", BTN_URL);
    DialogNode currentNode =
        new DialogNode(null, NODE_MESSAGE, ButtonType.REPLY, List.of(other, matched));

    doReturn(currentNode).when(dialogRepository).getNode(CURRENT_NODE_KEY);

    String result = nodeNavigator.getNextNodeKey(CHAT_ID, BTN_LABEL);

    assertEquals(BTN_NEXT, result);
  }

  @Test
  void getNextNodeKey_shouldReturnUserInput_whenCurrentNodeIsReplyAndButtonNotMatched() {
    Button button = new Button(BTN_LABEL, BTN_NEXT, null);
    DialogNode currentNode = new DialogNode(null, NODE_MESSAGE, ButtonType.REPLY, List.of(button));

    String wrongInput = "wrong";

    doReturn(currentNode).when(dialogRepository).getNode(CURRENT_NODE_KEY);

    String result = nodeNavigator.getNextNodeKey(CHAT_ID, wrongInput);

    assertEquals(wrongInput, result);
  }

  @Test
  void getNextNodeKey_shouldUseUserStateServiceWithStartDefault_whenResolvingCurrentNode() {
    doReturn(null).when(dialogRepository).getNode(CURRENT_NODE_KEY);

    nodeNavigator.getNextNodeKey(CHAT_ID, USER_INPUT);

    verify(userStateService).getUserStateOrDefault(CHAT_ID, StartCommand.COMMAND_NAME);
    verify(dialogRepository).getNode(CURRENT_NODE_KEY);
    verifyNoInteractions(nodeExecutor);
  }
}
