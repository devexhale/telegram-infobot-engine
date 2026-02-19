package repository;

import static org.junit.jupiter.api.Assertions.*;

import com.github.jawisimo.tbcfstarter.config.redis.RedisRepositoriesConfig;
import com.github.jawisimo.tbcfstarter.interaction.node.model.UserState;
import com.github.jawisimo.tbcfstarter.repository.RedisUserStateRepository;
import container.WithRedisTestContainer;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@DataRedisTest
@WithRedisTestContainer
@ContextConfiguration(classes = {RedisRepositoriesConfig.class, RedisUserStateRepository.class})
@TestPropertySource(properties = {"telegram.bot.user-state-persistent=true"})
class RedisUserStateRepositoryIT {

  @Autowired private RedisUserStateRepository repository;
  @Autowired private RedisConnectionFactory connectionFactory;

  private String chatId;
  private String nodeId;
  private UserState userState;

  @BeforeEach
  void setUp() {
    connectionFactory.getConnection().serverCommands().flushDb();

    chatId = "chat-1";
    nodeId = "node-1";
    userState = new UserState(chatId, nodeId);
  }

  @Test
  void shouldReturnEmptyOptional_whenUserStateDoesNotExist() {
    Optional<UserState> result = repository.findById(chatId);

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldSaveAndFindUserState_whenUserStateWasSaved() {
    repository.save(userState);

    Optional<UserState> result = repository.findById(chatId);

    assertTrue(result.isPresent());
    assertEquals(chatId, result.get().getChatId());
    assertEquals(nodeId, result.get().getNodeId());
  }

  @Test
  void shouldOverwriteUserState_whenSavedTwiceWithSameChatId() {
    String firstNodeId = "node-1";
    String secondNodeId = "node-2";

    UserState first = new UserState(chatId, firstNodeId);
    UserState second = new UserState(chatId, secondNodeId);

    repository.save(first);
    repository.save(second);

    Optional<UserState> result = repository.findById(chatId);

    assertTrue(result.isPresent());
    assertEquals(chatId, result.get().getChatId());
    assertEquals(secondNodeId, result.get().getNodeId());
  }

  @Test
  void shouldSaveAndFindUserState_whenNodeIdIsNull() {
    UserState stateWithNullNodeId = new UserState(chatId, null);

    repository.save(stateWithNullNodeId);

    Optional<UserState> result = repository.findById(chatId);

    assertTrue(result.isPresent());
    assertEquals(chatId, result.get().getChatId());
    assertNull(result.get().getNodeId());
  }
}
