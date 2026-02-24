package com.github.jawisimo.tbcfstarter.repository;

import com.github.jawisimo.tbcfstarter.interaction.node.model.UserState;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUserStateRepository implements UserStateRepository {
  private final ConcurrentHashMap<String, UserState> userStates = new ConcurrentHashMap<>();

  @Override
  public Optional<UserState> findById(String id) {
    return Optional.ofNullable(userStates.get(id));
  }

  @Override
  public <S extends UserState> S save(S userState) {
    userStates.put(userState.getChatId(), userState);
    return userState;
  }
}
