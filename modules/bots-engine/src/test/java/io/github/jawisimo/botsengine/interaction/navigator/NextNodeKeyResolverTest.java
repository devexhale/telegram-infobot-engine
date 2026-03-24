package io.github.jawisimo.botsengine.interaction.navigator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import io.github.jawisimo.botsengine.interaction.command.commandset.StartCommand;
import io.github.jawisimo.botsengine.interaction.navigator.dto.NavigationRequest;
import io.github.jawisimo.botsengine.model.Button;
import io.github.jawisimo.botsengine.model.ButtonType;
import io.github.jawisimo.botsengine.model.DialogNode;
import io.github.jawisimo.botsengine.repository.DialogRepository;
import io.github.jawisimo.botsengine.service.UserStateService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NextNodeKeyResolverTest {

  private static final String CHAT_ID = "130";
  private static final String USER_INPUT = "History";
  private static final String NODE_KEY = "history_q1";
  private static final String CURRENT_NODE_KEY = "node_1";
  private static final String MESSAGE = "Some message...";

  @Mock private UserStateService userStateService;
  @Mock private DialogRepository dialogRepository;

  @InjectMocks private NextNodeKeyResolver resolver;

  @Test
  void resolve_shouldReturnNextNodeKeyAndMarkFromButton_whenReplyButtonMatchesUserInput() {
    Button button = new Button(USER_INPUT, NODE_KEY, null);
    DialogNode node = new DialogNode(null, MESSAGE, ButtonType.REPLY, List.of(button));

    when(userStateService.getUserStateOrDefault(CHAT_ID, StartCommand.COMMAND_NAME))
        .thenReturn(CURRENT_NODE_KEY);
    when(dialogRepository.getNode(CURRENT_NODE_KEY)).thenReturn(node);

    NavigationRequest result = resolver.resolve(CHAT_ID, USER_INPUT);

    assertEquals(NODE_KEY, result.nodeKey());
    assertEquals(USER_INPUT, result.rawInput());
    assertTrue(result.fromButton());
  }

  @Test
  void resolve_shouldReturnRawInput_whenReplyButtonDoesNotMatchUserInput() {
    Button button = new Button("Other", NODE_KEY, null);
    DialogNode node = new DialogNode(null, MESSAGE, ButtonType.REPLY, List.of(button));

    when(userStateService.getUserStateOrDefault(CHAT_ID, StartCommand.COMMAND_NAME))
        .thenReturn(CURRENT_NODE_KEY);
    when(dialogRepository.getNode(CURRENT_NODE_KEY)).thenReturn(node);

    NavigationRequest result = resolver.resolve(CHAT_ID, USER_INPUT);

    assertEquals(USER_INPUT, result.nodeKey());
    assertEquals(USER_INPUT, result.rawInput());
    assertFalse(result.fromButton());
  }

  @Test
  void resolve_shouldReturnRawInput_whenCurrentNodeIsNull() {
    when(userStateService.getUserStateOrDefault(CHAT_ID, StartCommand.COMMAND_NAME))
        .thenReturn(CURRENT_NODE_KEY);
    when(dialogRepository.getNode(CURRENT_NODE_KEY)).thenReturn(null);

    NavigationRequest result = resolver.resolve(CHAT_ID, USER_INPUT);

    assertEquals(USER_INPUT, result.nodeKey());
    assertEquals(USER_INPUT, result.rawInput());
    assertFalse(result.fromButton());
  }

  @Test
  void resolve_shouldReturnRawInput_whenButtonTypeIsNotReply() {
    Button button = new Button(USER_INPUT, NODE_KEY, null);
    DialogNode node = new DialogNode(null, MESSAGE, ButtonType.INLINE, List.of(button));

    when(userStateService.getUserStateOrDefault(CHAT_ID, StartCommand.COMMAND_NAME))
        .thenReturn(CURRENT_NODE_KEY);
    when(dialogRepository.getNode(CURRENT_NODE_KEY)).thenReturn(node);

    NavigationRequest result = resolver.resolve(CHAT_ID, USER_INPUT);

    assertEquals(USER_INPUT, result.nodeKey());
    assertEquals(USER_INPUT, result.rawInput());
    assertFalse(result.fromButton());
  }

  @Test
  void resolve_shouldReturnRawInput_whenReplyNodeHasNoButtons() {
    DialogNode node = new DialogNode(null, MESSAGE, ButtonType.REPLY, null);

    when(userStateService.getUserStateOrDefault(CHAT_ID, StartCommand.COMMAND_NAME))
        .thenReturn(CURRENT_NODE_KEY);
    when(dialogRepository.getNode(CURRENT_NODE_KEY)).thenReturn(node);

    NavigationRequest result = resolver.resolve(CHAT_ID, USER_INPUT);

    assertEquals(USER_INPUT, result.nodeKey());
    assertEquals(USER_INPUT, result.rawInput());
    assertFalse(result.fromButton());
  }

  @Test
  void resolve_shouldUseStartCommandNode_whenUserStateIsMissing() {
    Button button = new Button(USER_INPUT, NODE_KEY, null);
    DialogNode node = new DialogNode(null, MESSAGE, ButtonType.REPLY, List.of(button));

    when(userStateService.getUserStateOrDefault(CHAT_ID, StartCommand.COMMAND_NAME))
        .thenReturn(StartCommand.COMMAND_NAME);
    when(dialogRepository.getNode(StartCommand.COMMAND_NAME)).thenReturn(node);

    NavigationRequest result = resolver.resolve(CHAT_ID, USER_INPUT);

    assertEquals(NODE_KEY, result.nodeKey());
    assertTrue(result.fromButton());
  }
}
