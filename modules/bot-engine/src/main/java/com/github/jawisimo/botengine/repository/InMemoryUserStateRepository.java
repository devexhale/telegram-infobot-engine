package com.github.jawisimo.botengine.repository;

import com.github.jawisimo.botengine.interaction.node.model.UserState;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUserStateRepository implements UserStateRepository {

  private final ConcurrentHashMap<String, UserState> userStates = new ConcurrentHashMap<>();

  @Override
  public Optional<UserState> findByChatId(String chatId) {
    return Optional.ofNullable(userStates.get(chatId));
  }

  @Override
  public <S extends UserState> S save(S userState) {
    userStates.put(userState.getChatId(), userState);
    return userState;
  }
}
