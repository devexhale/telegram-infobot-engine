package io.github.jawisimo.botsengine.repository;

import io.github.jawisimo.botsengine.model.UserState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

abstract class BaseUserStateRepositoryTest {

  protected abstract UserStateRepository getRepository();

  protected abstract void cleanUp();

  @BeforeEach
  void setUp() {
    cleanUp();
  }

  @Test
  void findByChatId_shouldReturnEmptyOptional_whenUserStateDoesNotExist() {
    String chatId = "chat-45";
    Optional<UserState> result = getRepository().findByChatId(chatId);
    assertTrue(result.isEmpty());
  }

  @Test
  void findByChatId_shouldReturnSavedUserState_whenUserStateWasSaved() {
    String chatId = "chat-24";
    String nodeId = "history";
    UserState userState = new UserState(chatId, nodeId);

    getRepository().save(userState);
    Optional<UserState> result = getRepository().findByChatId(chatId);

    assertTrue(result.isPresent());
    assertEquals(userState, result.get());
  }

  @Test
  void findByChatId_shouldReturnLastSavedUserState_whenUserStateWasOverwritten() {
    String chatId = "chat-25";

    UserState first = new UserState(chatId, "history");
    UserState second = new UserState(chatId, "move");

    getRepository().save(first);
    getRepository().save(second);

    Optional<UserState> result = getRepository().findByChatId(chatId);

    assertTrue(result.isPresent());
    assertEquals(second, result.get());
  }

  @Test
  void shouldKeepIndependentStates_whenUserStatesSavedForDifferentChats() {
    String firstChatId = "chat-1435576";
    String firstNodeId = "history";
    String secondChatId = "chat-2234455";
    String secondNodeId = "move";

    UserState firstUserState = new UserState(firstChatId, firstNodeId);
    UserState secondUserState = new UserState(secondChatId, secondNodeId);

    getRepository().save(firstUserState);
    getRepository().save(secondUserState);

    Optional<UserState> firstResult = getRepository().findByChatId(firstChatId);
    Optional<UserState> secondResult = getRepository().findByChatId(secondChatId);

    assertTrue(firstResult.isPresent());
    assertEquals(firstUserState, firstResult.get());
    assertTrue(secondResult.isPresent());
    assertEquals(secondUserState, secondResult.get());
  }
}
