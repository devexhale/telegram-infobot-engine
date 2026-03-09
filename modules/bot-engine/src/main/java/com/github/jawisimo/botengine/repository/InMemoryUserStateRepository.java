package com.github.jawisimo.botengine.repository;

import com.github.jawisimo.botengine.model.UserState;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

/**
 * In-memory implementation of {@link UserStateRepository}.
 *
 * <p>Stores user dialog state in a concurrent map for the duration of the application runtime.
 * Intended for non-persistent state management when external storage is not configured.
 *
 * <p>Thread-safe for concurrent access. All stored state is lost on application restart.
 *
 * @since 1.0
 */
@Repository
public class InMemoryUserStateRepository implements UserStateRepository {

  private final ConcurrentHashMap<String, UserState> userStates = new ConcurrentHashMap<>();

  /**
   * Returns the stored user state for the specified chat.
   *
   * @param chatId the chat identifier
   * @return an {@link Optional} containing the user state, or empty if none exists
   */
  @Override
  public Optional<UserState> findByChatId(String chatId) {
    return Optional.ofNullable(userStates.get(chatId));
  }

  /**
   * Persists the provided user state in memory.
   *
   * @param userState the user state to store
   * @param <S> the concrete type of {@link UserState}
   * @return the stored user state
   */
  @Override
  public <S extends UserState> S save(S userState) {
    userStates.put(userState.getChatId(), userState);
    return userState;
  }
}
