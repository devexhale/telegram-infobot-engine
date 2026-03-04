package com.github.jawisimo.botengine.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

/**
 * In-memory implementation of {@link MessageRepository}.
 *
 * <p>Stores message identifiers in a concurrent map scoped to the application runtime. Intended for
 * non-persistent message tracking during dialog execution.
 *
 * <p>Thread-safe for concurrent access. Stored data is lost when the application stops.
 *
 * @since 1.0
 */
@Repository
public class InMemoryMessageRepository implements MessageRepository {

  private final ConcurrentHashMap<String, List<Integer>> storage = new ConcurrentHashMap<>();

  /**
   * Stores a message identifier for the specified chat.
   *
   * @param chatId the chat identifier
   * @param messageId the message identifier to store
   */
  @Override
  public void save(String chatId, Integer messageId) {
    storage.compute(
        chatId,
        (k, v) -> {
          if (v == null) {
            return new ArrayList<>(List.of(messageId));
          }
          v.add(messageId);
          return v;
        });
  }

  /**
   * Removes and returns all message identifiers associated with the chat.
   *
   * @param chatId the chat identifier
   * @return the removed message identifiers or {@code empty List} if none were stored
   */
  @Override
  public List<Integer> removeAll(String chatId) {
    List<Integer> snapshot = new ArrayList<>();
    storage.computeIfPresent(
        chatId,
        (k, v) -> {
          snapshot.addAll(v);
          return null;
        });
    return List.copyOf(snapshot);
  }
}
