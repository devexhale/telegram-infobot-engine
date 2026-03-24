package io.github.jawisimo.botsengine.interaction.navigator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import io.github.jawisimo.botsengine.interaction.navigator.dto.NavigationRequest;
import io.github.jawisimo.botsengine.interaction.navigator.dto.NavigationResult;
import io.github.jawisimo.botsengine.model.DialogNode;
import io.github.jawisimo.botsengine.repository.DialogRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NodeRouterTest {

  private static final String CHAT_ID = "125L";
  private static final String NODE_KEY = "history_q1";
  private static final String USER_INPUT = "History";

  @Mock private DialogRepository dialogRepository;
  @Mock private NodeExecutor nodeExecutor;

  @InjectMocks private NodeRouter nodeRouter;

  @Test
  void route_shouldExecuteNodeAndReturnSuccess_whenNodeExists() {
    NavigationRequest request = new NavigationRequest(USER_INPUT, NODE_KEY, true);
    DialogNode node = new DialogNode(null, "message", null, List.of());

    when(dialogRepository.getNode(NODE_KEY)).thenReturn(node);

    NavigationResult result = nodeRouter.route(CHAT_ID, request);

    verify(dialogRepository).getNode(NODE_KEY);
    verify(nodeExecutor).execute(node, CHAT_ID);
    assertEquals(NavigationResult.SUCCESS, result);
  }

  @Test
  void route_shouldReturnNodeNotFound_whenNodeDoesNotExistAndRequestIsFromButton() {
    NavigationRequest request = new NavigationRequest(USER_INPUT, NODE_KEY, true);

    when(dialogRepository.getNode(NODE_KEY)).thenReturn(null);

    NavigationResult result = nodeRouter.route(CHAT_ID, request);

    verify(dialogRepository).getNode(NODE_KEY);
    verifyNoInteractions(nodeExecutor);
    assertEquals(NavigationResult.NODE_NOT_FOUND, result);
  }

  @Test
  void route_shouldReturnIrrelevantInput_whenNodeDoesNotExistAndRequestIsNotFromButton() {
    NavigationRequest request = new NavigationRequest(USER_INPUT, NODE_KEY, false);

    when(dialogRepository.getNode(NODE_KEY)).thenReturn(null);

    NavigationResult result = nodeRouter.route(CHAT_ID, request);

    verify(dialogRepository).getNode(NODE_KEY);
    verifyNoInteractions(nodeExecutor);
    assertEquals(NavigationResult.IRRELEVANT_INPUT, result);
  }
}
