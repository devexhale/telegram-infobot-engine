package com.github.jawisimo.tbcfstarter.service;

import com.github.jawisimo.tbcfstarter.interaction.node.model.UserState;
import com.github.jawisimo.tbcfstarter.repository.UserStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStateService {
  private final UserStateRepository userStateRepository;

  public String getUserStateOrDefault(String chatId, String defaultState) {
    return userStateRepository.findById(chatId).map(UserState::getNodeId).orElse(defaultState);
  }

  public void saveUserStateIfPersist(String chatId, String nextState) {
    userStateRepository.save(new UserState(chatId, nextState));
  }
}
