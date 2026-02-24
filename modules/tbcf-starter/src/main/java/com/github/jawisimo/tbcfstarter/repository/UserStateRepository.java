package com.github.jawisimo.tbcfstarter.repository;

import com.github.jawisimo.tbcfstarter.interaction.node.model.UserState;

import java.util.Optional;

public interface UserStateRepository {

  Optional<UserState> findById(String id);

  <S extends UserState> S save(S userState);
}
