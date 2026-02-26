package com.github.jawisimo.botengine.repository;

import com.github.jawisimo.botengine.interaction.node.model.UserState;

import java.util.Optional;

public interface UserStateRepository {

  Optional<UserState> findByChatId(String chatId);

  <S extends UserState> S save(S userState);
}
