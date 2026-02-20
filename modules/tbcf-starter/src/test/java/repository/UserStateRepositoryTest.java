package repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.github.jawisimo.tbcfstarter.config.BotProperties;
import com.github.jawisimo.tbcfstarter.interaction.node.model.UserState;
import com.github.jawisimo.tbcfstarter.repository.InMemoryUserStateRepository;
import com.github.jawisimo.tbcfstarter.repository.RedisUserStateRepository;
import com.github.jawisimo.tbcfstarter.repository.UserStateRepository;
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
class UserStateRepositoryTest {

  @Mock private BotProperties properties;
  @Mock private RedisUserStateRepository redisRepository;
  @Mock private InMemoryUserStateRepository inMemoryRepository;

  private UserStateRepository repository;

  private String chatId;
  private UserState userState;
  private UserState expected;

  @BeforeEach
  void setUp() {
    repository = new UserStateRepository(properties);

    chatId = "chat-95";
    userState = mock(UserState.class);
    expected = mock(UserState.class);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void shouldDelegateFindByIdToProperRepository_whenUserStatePersistentPropertyIsEvaluated(
      boolean persistent) {
    when(properties.userStatePersistent()).thenReturn(persistent);

    if (persistent) {
      when(redisRepository.findById(chatId)).thenReturn(Optional.of(expected));
    } else {
      when(inMemoryRepository.findById(chatId)).thenReturn(Optional.of(expected));
    }

    injectRepositories();

    Optional<UserState> result = repository.findById(chatId);

    assertEquals(Optional.of(expected), result);

    if (persistent) {
      verify(redisRepository).findById(chatId);
      verifyNoInteractions(inMemoryRepository);
    } else {
      verify(inMemoryRepository).findById(chatId);
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
    when(redisRepository.findById(chatId)).thenReturn(Optional.of(expected));

    injectRepositories();

    Optional<UserState> result = repository.findById(chatId);

    assertEquals(Optional.of(expected), result);
    verify(redisRepository).findById(chatId);
    verifyNoInteractions(inMemoryRepository);
  }

  @Test
  void shouldAssignOnlyInMemoryRepository_whenUserStatePersistentIsFalse() {
    when(properties.userStatePersistent()).thenReturn(false);
    when(inMemoryRepository.findById(chatId)).thenReturn(Optional.of(expected));

    injectRepositories();

    Optional<UserState> result = repository.findById(chatId);

    assertEquals(Optional.of(expected), result);
    verify(inMemoryRepository).findById(chatId);
    verifyNoInteractions(redisRepository);
  }

  private void injectRepositories() {
    invokeSetter("setRedisUserStateRepository", RedisUserStateRepository.class, redisRepository);
    invokeSetter(
        "setInMemoryUserStateRepository", InMemoryUserStateRepository.class, inMemoryRepository);
  }

  private <T> void invokeSetter(String methodName, Class<T> paramType, T argument) {
    try {
      Method method = UserStateRepository.class.getDeclaredMethod(methodName, paramType);
      method.setAccessible(true);
      method.invoke(repository, argument);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
