package io.github.jawisimo.botsengine.repository;

import io.github.jawisimo.botsengine.model.UserState;
import java.util.Optional;

/**
 * Defines a contract for persisting and retrieving user dialog state.
 *
 * <p>Provides access to the current dialog node associated with a chat identifier.
 *
 * @since 1.0
 */
public interface UserStateRepository {

  /**
   * Returns the stored {@link UserState} for the given chat identifier.
   *
   * @param chatId the chat identifier
   * @return an {@link Optional} containing the stored state, or empty if none exists
   */
  Optional<UserState> findByChatId(String chatId);

  /**
   * Persists the given {@link UserState}.
   *
   * @param userState the user state to persist
   * @param <S> the concrete type of {@link UserState}
   * @return the persisted user state
   */
  <S extends UserState> S save(S userState);
}
