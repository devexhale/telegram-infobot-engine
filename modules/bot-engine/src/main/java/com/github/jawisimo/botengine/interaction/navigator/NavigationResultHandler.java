package com.github.jawisimo.botengine.interaction.navigator;

import com.github.jawisimo.botengine.interaction.navigator.dto.NavigationRequest;
import com.github.jawisimo.botengine.interaction.navigator.dto.NavigationResult;
import com.github.jawisimo.botengine.service.UserStateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Handles the outcome of dialog node navigation.
 *
 * <p>Processes {@link NavigationResult} returned by the navigation layer and performs the
 * corresponding side effects, such as updating the user state or logging navigation errors.
 *
 * <p>If navigation succeeds, {@link UserStateService} persists the user's current dialog node. For
 * unresolved or irrelevant input, appropriate log entries are generated to assist with debugging
 * and monitoring dialog interactions.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NavigationResultHandler {

  private static final String DELETE_MESSAGE = "Message deleted from chat";

  private final UserStateService userStateService;

  /**
   * Processes the result of a dialog navigation attempt.
   *
   * <p>Updates the user state when navigation is successful or logs diagnostic information when the
   * requested node cannot be resolved or the user input is irrelevant to the current dialog
   * context.
   *
   * @param chatId the chat identifier
   * @param request the resolved navigation request containing the raw user input and target node
   * @param result the result of the navigation attempt
   */
  public void handle(String chatId, NavigationRequest request, NavigationResult result) {
    switch (result) {
      case SUCCESS -> userStateService.saveUserState(chatId, request.nodeKey());
      case NODE_NOTE_FOUND -> log.warn("Dialog node '{}' not found", request.nodeKey());
      case IRRELEVANT_INPUT ->
          log.warn(
              "Irrelevant message sent: '{}'. {}: '{}'",
              request.rawInput(),
              DELETE_MESSAGE,
              chatId);
    }
  }
}
