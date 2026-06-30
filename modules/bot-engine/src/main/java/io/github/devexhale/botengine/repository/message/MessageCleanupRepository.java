package io.github.devexhale.botengine.repository.message;

import java.util.Set;

/**
 * Repository for managing message identifiers slated for cleanup during dialog execution.
 *
 * <p>Tracks messages that should be removed when navigating between dialog nodes.
 *
 * @since 1.0
 */
public interface MessageCleanupRepository {

  /**
   * Stores a message identifier for the given chat.
   *
   * @param chatId the chat identifier
   * @param messageId the message identifier to store
   */
  void save(String chatId, Integer messageId);

  /**
   * Removes and returns all stored message identifiers for the given chat.
   *
   * @param chatId the chat identifier
   * @return the set of removed message identifiers
   */
  Set<Integer> deleteAllByChatId(String chatId);
}
