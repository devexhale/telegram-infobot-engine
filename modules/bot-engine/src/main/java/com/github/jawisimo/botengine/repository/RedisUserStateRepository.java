package com.github.jawisimo.botengine.repository;

import com.github.jawisimo.botengine.annotation.UserStatePersistent;
import com.github.jawisimo.botengine.interaction.node.model.UserState;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@UserStatePersistent
public interface RedisUserStateRepository
    extends CrudRepository<UserState, String>, UserStateRepository {

  @NotNull
  @Override
  Optional<UserState> findByChatId(@NotNull String chatId);

  @NotNull
  <S extends UserState> S save(@NotNull S userState);
}
