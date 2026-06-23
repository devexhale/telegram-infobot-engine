package io.github.devexhale.botengine.service;

import io.github.devexhale.botengine.repository.state.UserStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStateService {

  private final UserStateRepository userStateRepository;

  public String getUserStateOrDefault(String chatId, String defaultNodeId) {
    return userStateRepository.findNodeKey(chatId).orElse(defaultNodeId);
  }

  public void saveUserState(String chatId, String currentNodeId) {
    userStateRepository.save(chatId, currentNodeId);
  }

  public void deleteUserState(String chatId) {
    userStateRepository.delete(chatId);
  }
}
