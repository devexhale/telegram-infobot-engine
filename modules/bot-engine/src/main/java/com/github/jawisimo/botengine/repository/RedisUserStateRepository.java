package com.github.jawisimo.botengine.repository;

import com.github.jawisimo.botengine.annotation.UserStatePersistent;
import com.github.jawisimo.botengine.interaction.node.model.UserState;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Redis-backed implementation of {@link UserStateRepository}.
 *
 * <p>Extends {@link CrudRepository} to provide persistent storage of {@link UserState} using Spring
 * Data Redis. Activated only when {@link UserStatePersistent} is enabled.
 *
 * @since 1.0
 */
@Repository
@UserStatePersistent
public interface RedisUserStateRepository
    extends CrudRepository<UserState, String>, UserStateRepository {

  /**
   * Returns the stored user state for the specified chat identifier.
   *
   * @param chatId the chat identifier
   * @return an {@link Optional} containing the user state, or empty if none exists
   */
  @NotNull
  @Override
  Optional<UserState> findByChatId(@NotNull String chatId);

  /**
   * Persists the provided user state in Redis.
   *
   * @param userState the user state to store
   * @param <S> the concrete type of {@link UserState}
   * @return the persisted user state
   */
  @NotNull
  @Override
  <S extends UserState> S save(@NotNull S userState);
}
