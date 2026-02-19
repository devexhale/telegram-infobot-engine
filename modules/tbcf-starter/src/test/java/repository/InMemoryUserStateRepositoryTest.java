package repository;

import com.github.jawisimo.tbcfstarter.interaction.node.model.UserState;
import com.github.jawisimo.tbcfstarter.repository.InMemoryUserStateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InMemoryUserStateRepositoryTest {

  private InMemoryUserStateRepository repository;

  @BeforeEach
  void init() {
    repository = new InMemoryUserStateRepository();
  }

  @Test
  void findById_shouldReturnEmptyOptional_whenUserStateDoesNotExist() {
    String chatId = "chat-1";

    Optional<UserState> result = repository.findById(chatId);

    assertTrue(result.isEmpty());
  }

  @Test
  void findById_shouldReturnSavedUserState_whenUserStateWasSaved() {
    UserState userState = mock(UserState.class);
    String chatId = "chat-1";

    when(userState.getChatId()).thenReturn(chatId);

    repository.save(userState);

    Optional<UserState> result = repository.findById(chatId);

    assertTrue(result.isPresent());
    assertSame(userState, result.get());
  }

  @Test
  void findById_shouldReturnLastSavedUserState_whenUserStateWasSavedTwiceForSameChat() {
    UserState firstUserState = mock(UserState.class);
    UserState secondUserState = mock(UserState.class);
    String chatId = "chat-1";

    when(firstUserState.getChatId()).thenReturn(chatId);
    when(secondUserState.getChatId()).thenReturn(chatId);

    repository.save(firstUserState);
    repository.save(secondUserState);

    Optional<UserState> result = repository.findById(chatId);

    assertTrue(result.isPresent());
    assertSame(secondUserState, result.get());
  }

  @Test
  void shouldKeepIndependentStates_whenUserStatesSavedForDifferentChats() {
    UserState firstUserState = mock(UserState.class);
    UserState secondUserState = mock(UserState.class);
    String firstChatId = "chat-1";
    String secondChatId = "chat-2";

    when(firstUserState.getChatId()).thenReturn(firstChatId);
    when(secondUserState.getChatId()).thenReturn(secondChatId);

    repository.save(firstUserState);
    repository.save(secondUserState);

    Optional<UserState> firstResult = repository.findById(firstChatId);
    Optional<UserState> secondResult = repository.findById(secondChatId);

    assertTrue(firstResult.isPresent());
    assertSame(firstUserState, firstResult.get());
    assertTrue(secondResult.isPresent());
    assertSame(secondUserState, secondResult.get());
  }
}
