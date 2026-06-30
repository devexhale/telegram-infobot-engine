package io.github.devexhale.botengine.repository.state;

import java.util.Optional;

/**
 * Repository for managing the current dialog state of users.
 *
 * @since 1.0
 */
public interface UserStateRepository {

  /**
   * Finds the current dialog node key for the specified chat.
   *
   * @param chatId the chat identifier
   * @return an {@link Optional} containing the node key, or empty if not found
   */
  Optional<String> findNodeKey(String chatId);

  /**
   * Saves the current dialog node key for the specified chat.
   *
   * @param chatId the chat identifier
   * @param nodeId the dialog node key to save
   */
  void save(String chatId, String nodeId);

  /**
   * Deletes the dialog state for the specified chat.
   *
   * @param chatId the chat identifier
   */
  void delete(String chatId);
}
