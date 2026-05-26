package io.github.devexhale.botengine.service;

import io.github.devexhale.botengine.repository.state.UserStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStateService {

  private final UserStateRepository userStateRepository;

  /**
   * Returns the stored node ID for the user or the default value if none exists.
   *
   * @param chatId the unique Telegram chat identifier
   * @param defaultNodeId the default node ID to return when no stored state found
   * @return the current dialog node ID for the user
   */
  public String getUserStateOrDefault(String chatId, String defaultNodeId) {
    return userStateRepository.findNodeKey(chatId).orElse(defaultNodeId);
  }

  /**
   * Persists the dialog state for the given user.
   *
   * @param chatId the unique Telegram chat identifier
   * @param currentNodeId the identifier of the current dialog node
   */
  public void saveUserState(String chatId, String currentNodeId) {
    userStateRepository.save(chatId, currentNodeId);
  }

  public void deleteUserState(String chatId) {
    userStateRepository.delete(chatId);
  }
}
