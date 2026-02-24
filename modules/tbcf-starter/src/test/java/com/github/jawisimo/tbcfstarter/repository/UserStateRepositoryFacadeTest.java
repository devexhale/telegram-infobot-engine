package com.github.jawisimo.tbcfstarter.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.github.jawisimo.tbcfstarter.config.BotProperties;
import com.github.jawisimo.tbcfstarter.interaction.node.model.UserState;

import java.lang.reflect.Method;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserStateRepositoryFacadeTest {

  private static final String CHAT_ID = "chat-95";
  private static final String NODE_ID = "some_node";

  @Mock private BotProperties properties;
  @Mock private RedisUserStateRepository redisRepository;
  @Mock private InMemoryUserStateRepository inMemoryRepository;

  private UserStateRepositoryFacade repository;

  private UserState userState;
  private UserState expected;

  @BeforeEach
  void setUp() {
    repository = new UserStateRepositoryFacade(properties);

    userState = new UserState(CHAT_ID, NODE_ID);
    expected = userState;
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void shouldDelegateFindByIdToProperRepository_whenUserStatePersistentPropertyIsEvaluated(
      boolean persistent) {
    when(properties.userStatePersistent()).thenReturn(persistent);

    if (persistent) {
      when(redisRepository.findById(CHAT_ID)).thenReturn(Optional.of(expected));
    } else {
      when(inMemoryRepository.findById(CHAT_ID)).thenReturn(Optional.of(expected));
    }

    injectRepositories();

    Optional<UserState> result = repository.findById(CHAT_ID);

    assertEquals(Optional.of(expected), result);

    if (persistent) {
      verify(redisRepository).findById(CHAT_ID);
      verifyNoInteractions(inMemoryRepository);
    } else {
      verify(inMemoryRepository).findById(CHAT_ID);
      verifyNoInteractions(redisRepository);
    }
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void shouldDelegateSaveToProperRepository_whenUserStatePersistentPropertyIsEvaluated(
      boolean persistent) {
    when(properties.userStatePersistent()).thenReturn(persistent);

    injectRepositories();

    repository.save(userState);

    if (persistent) {
      verify(redisRepository).save(userState);
      verifyNoInteractions(inMemoryRepository);
    } else {
      verify(inMemoryRepository).save(userState);
      verifyNoInteractions(redisRepository);
    }
  }

  @Test
  void shouldAssignOnlyRedisRepository_whenUserStatePersistentIsTrue() {
    when(properties.userStatePersistent()).thenReturn(true);
    when(redisRepository.findById(CHAT_ID)).thenReturn(Optional.of(expected));

    injectRepositories();

    Optional<UserState> result = repository.findById(CHAT_ID);

    assertEquals(Optional.of(expected), result);
    verify(redisRepository).findById(CHAT_ID);
    verifyNoInteractions(inMemoryRepository);
  }

  @Test
  void shouldAssignOnlyInMemoryRepository_whenUserStatePersistentIsFalse() {
    when(properties.userStatePersistent()).thenReturn(false);
    when(inMemoryRepository.findById(CHAT_ID)).thenReturn(Optional.of(expected));

    injectRepositories();

    Optional<UserState> result = repository.findById(CHAT_ID);

    assertEquals(Optional.of(expected), result);
    verify(inMemoryRepository).findById(CHAT_ID);
    verifyNoInteractions(redisRepository);
  }

  private void injectRepositories() {
    invokeSetter("setRedisUserStateRepository", RedisUserStateRepository.class, redisRepository);
    invokeSetter(
        "setInMemoryUserStateRepository", InMemoryUserStateRepository.class, inMemoryRepository);
  }

  private <T> void invokeSetter(String methodName, Class<T> paramType, T argument) {
    try {
      Method method = UserStateRepositoryFacade.class.getDeclaredMethod(methodName, paramType);
      method.setAccessible(true);
      method.invoke(repository, argument);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
