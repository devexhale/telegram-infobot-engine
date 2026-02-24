package com.github.jawisimo.tbcfstarter.service;

import com.github.jawisimo.tbcfstarter.interaction.node.model.UserState;
import com.github.jawisimo.tbcfstarter.repository.UserStateRepositoryFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStateService {
  private final UserStateRepositoryFacade userStateRepositoryFacade;

  public String getUserStateOrDefault(String chatId, String defaultState) {
    return userStateRepositoryFacade
        .findById(chatId)
        .map(UserState::getNodeId)
        .orElse(defaultState);
  }

  public void saveUserState(String chatId, String nextState) {
    userStateRepositoryFacade.save(new UserState(chatId, nextState));
  }
}
