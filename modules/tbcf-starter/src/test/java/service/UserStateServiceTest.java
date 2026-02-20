package service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.github.jawisimo.tbcfstarter.interaction.node.model.UserState;
import com.github.jawisimo.tbcfstarter.repository.UserStateRepository;
import com.github.jawisimo.tbcfstarter.service.UserStateService;
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

  @Mock private UserStateRepository userStateRepository;

  @InjectMocks private UserStateService service;

  @ParameterizedTest
  @MethodSource("provideUserStateScenarios")
  void getUserStateOrDefault_shouldReturnCorrectState(
      UserState repositoryState, String defaultState, String expectedResult) {

    String chatId = "chat-1";
    Optional<UserState> repositoryResult = Optional.ofNullable(repositoryState);
    when(userStateRepository.findById(chatId)).thenReturn(repositoryResult);

    String actualResult = service.getUserStateOrDefault(chatId, defaultState);

    assertEquals(expectedResult, actualResult);
    verify(userStateRepository).findById(chatId);
  }

  static Stream<Arguments> provideUserStateScenarios() {
    return Stream.of(
        Arguments.of(null, "/start", "/start"),
        Arguments.of(new UserState(CHAT_ID, MENU), START, MENU),
        Arguments.of(new UserState(CHAT_ID, null), START, START),
        Arguments.of(new UserState(CHAT_ID, ""), START, ""),
        Arguments.of(null, null, null));
  }

  @Test
  void saveUserState_shouldSaveNewUserState() {
    service.saveUserState(CHAT_ID, MENU);

    ArgumentCaptor<UserState> captor = ArgumentCaptor.forClass(UserState.class);
    verify(userStateRepository).save(captor.capture());

    UserState saved = captor.getValue();
    assertEquals(CHAT_ID, saved.getChatId());
    assertEquals(MENU, saved.getNodeId());
  }

  @Test
  void getUserStateOrDefault_shouldCallRepositoryForEachInvocation() {
    String defaultState = START;
    int expectedInvocations = 2;

    when(userStateRepository.findById(CHAT_ID))
        .thenReturn(Optional.of(new UserState(CHAT_ID, MENU)));

    service.getUserStateOrDefault(CHAT_ID, defaultState);
    service.getUserStateOrDefault(CHAT_ID, defaultState);

    verify(userStateRepository, times(expectedInvocations)).findById(CHAT_ID);
  }
}
