package io.github.devexhale.botengine.repository.subscribe;

import java.util.Set;

/**
 * Repository for managing bot subscribers.
 *
 * @since 1.0
 */
public interface SubscriberRepository {

  /**
   * Saves a chat ID as a subscriber.
   *
   * @param chatId the chat identifier to save
   */
  void save(String chatId);

  /**
   * Removes a chat ID from the subscribers.
   *
   * @param chatId the chat identifier to remove
   */
  void delete(String chatId);

  /**
   * Retrieves all subscribed chat IDs.
   *
   * @return a set of all subscriber chat identifiers
   */
  Set<String> getAll();
}
