package io.github.devexhale.botengine.repository.message;

import java.util.Set;

/**
 * Stores identifiers of messages associated with a chat during dialog execution.
 *
 * <p>Used to track messages that should be removed when navigating between dialog nodes.
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
   * @return the set of removed message identifiers, or an empty list if none exist
   */
  Set<Integer> deleteAllByChatId(String chatId);
}
