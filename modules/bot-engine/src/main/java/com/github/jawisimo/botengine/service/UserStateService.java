package com.github.jawisimo.botengine.service;

import com.github.jawisimo.botengine.model.UserState;
import com.github.jawisimo.botengine.repository.UserStateRepositoryFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Provides access to persistent user dialog state within the framework.
 *
 * <p>Encapsulates interaction with {@link UserStateRepositoryFacade} and abstracts the underlying
 * storage mechanism (in-memory or Redis). Used during dialog execution to resolve and persist the
 * current dialog node.
 *
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class UserStateService {

  private final UserStateRepositoryFacade userStateRepositoryFacade;

  /**
   * Returns the stored node ID for the user or the default value if none exists.
   *
   * @param chatId the unique Telegram chat identifier
   * @param defaultNodeId the default node ID to return when no stored state found
   * @return the current dialog node ID for the user
   */
  public String getUserStateOrDefault(String chatId, String defaultNodeId) {
    return userStateRepositoryFacade
        .findByChatId(chatId)
        .map(UserState::getNodeId)
        .orElse(defaultNodeId);
  }

  /**
   * Persists the dialog state for the given user.
   *
   * @param chatId the unique Telegram chat identifier
   * @param currentNodeId the identifier of the current dialog node
   */
  public void saveUserState(String chatId, String currentNodeId) {
    userStateRepositoryFacade.save(new UserState(chatId, currentNodeId));
  }
}
