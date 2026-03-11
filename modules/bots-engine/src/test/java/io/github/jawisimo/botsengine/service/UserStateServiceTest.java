package io.github.jawisimo.botsengine.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.jawisimo.botsengine.model.UserState;
import io.github.jawisimo.botsengine.repository.UserStateRepositoryFacade;

import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserStateServiceTest {

  private static final String CHAT_ID = "chatId-555";
  private static final String START = "/start";
  private static final String MENU = "/menu";

  @Mock private UserStateRepositoryFacade userStateRepositoryFacade;

  @InjectMocks private UserStateService service;

  @ParameterizedTest
  @MethodSource("provideUserStateScenarios")
  void getUserStateOrDefault_shouldReturnCorrectState(
      UserState repositoryState, String defaultState, String expectedResult) {
    Optional<UserState> repositoryResult = Optional.ofNullable(repositoryState);
    when(userStateRepositoryFacade.findByChatId(CHAT_ID)).thenReturn(repositoryResult);

    String actualResult = service.getUserStateOrDefault(CHAT_ID, defaultState);

    assertEquals(expectedResult, actualResult);
    verify(userStateRepositoryFacade).findByChatId(CHAT_ID);
  }

  @Test
  void saveUserState_shouldSaveNewUserState() {
    service.saveUserState(CHAT_ID, MENU);

    ArgumentCaptor<UserState> captor = ArgumentCaptor.forClass(UserState.class);
    verify(userStateRepositoryFacade).save(captor.capture());

    UserState saved = captor.getValue();
    assertEquals(CHAT_ID, saved.getChatId());
    assertEquals(MENU, saved.getNodeId());
  }

  @Test
  void getUserStateOrDefault_shouldCallRepositoryForEachInvocation() {
    String defaultState = START;
    int expectedInvocations = 2;

    when(userStateRepositoryFacade.findByChatId(CHAT_ID))
        .thenReturn(Optional.of(new UserState(CHAT_ID, MENU)));

    service.getUserStateOrDefault(CHAT_ID, defaultState);
    service.getUserStateOrDefault(CHAT_ID, defaultState);

    verify(userStateRepositoryFacade, times(expectedInvocations)).findByChatId(CHAT_ID);
  }

  static Stream<Arguments> provideUserStateScenarios() {
    return Stream.of(
        Arguments.of(null, START, START),
        Arguments.of(new UserState(CHAT_ID, MENU), START, MENU),
        Arguments.of(new UserState(CHAT_ID, null), START, START),
        Arguments.of(new UserState(CHAT_ID, ""), START, ""),
        Arguments.of(null, null, null));
  }
}
