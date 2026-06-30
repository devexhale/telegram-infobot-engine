package io.github.devexhale.botengine.service;

import io.github.devexhale.botengine.repository.state.UserStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Manages the current dialog state for users.
 *
 * <p>Delegates state persistence to {@link UserStateRepository}.
 *
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class UserStateService {

  private final UserStateRepository userStateRepository;

  /**
   * Retrieves the current dialog node key for the specified chat, or returns the default value if
   * not found.
   *
   * @param chatId the chat identifier
   * @param defaultNodeId the default node key to return if state is absent
   * @return the current node key or the default value
   */
  public String getUserStateOrDefault(String chatId, String defaultNodeId) {
    return userStateRepository.findNodeKey(chatId).orElse(defaultNodeId);
  }

  /**
   * Saves the current dialog node key for the specified chat.
   *
   * @param chatId the chat identifier
   * @param currentNodeId the dialog node key to save
   */
  public void saveUserState(String chatId, String currentNodeId) {
    userStateRepository.save(chatId, currentNodeId);
  }

  /**
   * Deletes the dialog state for the specified chat.
   *
   * @param chatId the chat identifier
   */
  public void deleteUserState(String chatId) {
    userStateRepository.delete(chatId);
  }
}
