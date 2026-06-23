package io.github.devexhale.botengine.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.devexhale.botengine.repository.state.UserStateRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserStateServiceTest {

  private static final String CHAT_ID = "123456789";
  private static final String STORED_NODE_ID = "stored_node";
  private static final String DEFAULT_NODE_ID = "/start";

  @Mock private UserStateRepository userStateRepository;

  @InjectMocks private UserStateService userStateService;

  @Test
  void getUserStateOrDefault_shouldReturnStoredValue_whenNodeExists() {
    when(userStateRepository.findNodeKey(CHAT_ID)).thenReturn(Optional.of(STORED_NODE_ID));

    String result = userStateService.getUserStateOrDefault(CHAT_ID, DEFAULT_NODE_ID);

    assertEquals(STORED_NODE_ID, result);
  }

  @Test
  void getUserStateOrDefault_shouldReturnDefaultValue_whenNodeDoesNotExist() {
    when(userStateRepository.findNodeKey(CHAT_ID)).thenReturn(Optional.empty());

    String result = userStateService.getUserStateOrDefault(CHAT_ID, DEFAULT_NODE_ID);

    assertEquals(DEFAULT_NODE_ID, result);
  }

  @Test
  void saveUserState_shouldDelegateToRepository_whenCalled() {
    userStateService.saveUserState(CHAT_ID, STORED_NODE_ID);

    verify(userStateRepository).save(CHAT_ID, STORED_NODE_ID);
  }

  @Test
  void deleteUserState_shouldDelegateToRepository_whenCalled() {
    userStateService.deleteUserState(CHAT_ID);

    verify(userStateRepository).delete(CHAT_ID);
  }
}
