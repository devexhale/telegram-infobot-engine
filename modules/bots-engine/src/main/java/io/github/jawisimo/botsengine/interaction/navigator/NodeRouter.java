package io.github.jawisimo.botsengine.interaction.navigator;

import static io.github.jawisimo.botsengine.interaction.navigator.dto.NavigationResult.*;

import io.github.jawisimo.botsengine.model.DialogNode;
import io.github.jawisimo.botsengine.interaction.navigator.dto.NavigationRequest;
import io.github.jawisimo.botsengine.interaction.navigator.dto.NavigationResult;
import io.github.jawisimo.botsengine.repository.DialogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Routes navigation requests to dialog nodes and triggers their execution.
 *
 * <p>Resolves the target {@link DialogNode} using {@link DialogRepository}. The node key is
 * obtained from {@link NavigationRequest}.
 *
 * <p>If a matching node is found, execution is delegated to {@link NodeExecutor}.
 *
 * <p>If the node cannot be resolved, the result depends on the origin of the request. If the
 * request originated from a button interaction, the missing node is treated as a dialog
 * configuration error and {@link NavigationResult#NODE_NOT_FOUND} is returned.
 *
 * <p>If the request originated from raw user input, the input is considered irrelevant to the
 * current dialog context and {@link NavigationResult#IRRELEVANT_INPUT} is returned.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class NodeRouter {

  private final DialogRepository dialogRepository;
  private final NodeExecutor nodeExecutor;

  /**
   * Attempts to navigate to the dialog node specified in the navigation request.
   *
   * <p>If a matching {@link DialogNode} exists, it is executed and {@link NavigationResult#SUCCESS}
   * is returned.
   *
   * <p>If the node does not exist, the result depends on the origin of the request.
   *
   * @param chatId the chat identifier
   * @param request the navigation request containing the resolved node key
   * @return the result of the navigation attempt
   */
  public NavigationResult route(String chatId, NavigationRequest request) {
    DialogNode node = dialogRepository.getNode(request.nodeKey());

    if (node == null) {
      return request.fromButton() ? NODE_NOT_FOUND : IRRELEVANT_INPUT;
    }

    nodeExecutor.execute(node, chatId);
    return SUCCESS;
  }
}
