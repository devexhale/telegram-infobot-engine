package com.github.jawisimo.botengine.service;

import com.github.jawisimo.botengine.interaction.node.model.UserState;
import com.github.jawisimo.botengine.repository.UserStateRepositoryFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStateService {
  private final UserStateRepositoryFacade userStateRepositoryFacade;

  public String getUserStateOrDefault(String chatId, String defaultState) {
    return userStateRepositoryFacade
        .findByChatId(chatId)
        .map(UserState::getNodeId)
        .orElse(defaultState);
  }

  public void saveUserState(String chatId, String nextState) {
    userStateRepositoryFacade.save(new UserState(chatId, nextState));
  }
}
