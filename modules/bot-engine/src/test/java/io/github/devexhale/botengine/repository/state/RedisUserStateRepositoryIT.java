package io.github.devexhale.botengine.repository.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.devexhale.botengine.testsupport.container.WithRedisTestContainer;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;

@DataRedisTest(excludeAutoConfiguration = RedisRepositoriesAutoConfiguration.class)
@WithRedisTestContainer
@ContextConfiguration(classes = {RedisUserStateRepository.class})
class RedisUserStateRepositoryIT {

  private static final String CHAT_ID_1 = "123456789";
  private static final String CHAT_ID_2 = "987654321";
  private static final String NODE_ID_1 = "start_node";
  private static final String NODE_ID_2 = "next_node";
  private static final String KEY_PREFIX = "bot:user:state:";

  @Autowired private RedisUserStateRepository repository;
  @Autowired private RedisTemplate<String, String> redisTemplate;

  @AfterEach
  void clearDb() {
    Set<String> keys = redisTemplate.keys(KEY_PREFIX + "*");

    if (!keys.isEmpty()) {
      redisTemplate.delete(keys);
    }
  }

  @Test
  void save_shouldStoreNodeId_whenCalled() {
    repository.save(CHAT_ID_1, NODE_ID_1);

    Optional<String> result = repository.findNodeKey(CHAT_ID_1);

    assertTrue(result.isPresent());
    assertEquals(NODE_ID_1, result.get());
  }

  @Test
  void save_shouldOverwriteExistingNodeId_whenCalledTwiceForSameChat() {
    repository.save(CHAT_ID_1, NODE_ID_1);
    repository.save(CHAT_ID_1, NODE_ID_2);

    Optional<String> result = repository.findNodeKey(CHAT_ID_1);

    assertTrue(result.isPresent());
    assertEquals(NODE_ID_2, result.get());
  }

  @Test
  void save_shouldIsolateDataBetweenDifferentChats_whenCalled() {
    repository.save(CHAT_ID_1, NODE_ID_1);
    repository.save(CHAT_ID_2, NODE_ID_2);

    Optional<String> result1 = repository.findNodeKey(CHAT_ID_1);
    Optional<String> result2 = repository.findNodeKey(CHAT_ID_2);

    assertTrue(result1.isPresent());
    assertEquals(NODE_ID_1, result1.get());
    assertTrue(result2.isPresent());
    assertEquals(NODE_ID_2, result2.get());
  }

  @Test
  void findNodeKey_shouldReturnEmpty_whenChatIdDoesNotExist() {
    Optional<String> result = repository.findNodeKey(CHAT_ID_1);

    assertTrue(result.isEmpty());
  }

  @Test
  void delete_shouldRemoveNodeId_whenCalled() {
    repository.save(CHAT_ID_1, NODE_ID_1);
    repository.delete(CHAT_ID_1);

    Optional<String> result = repository.findNodeKey(CHAT_ID_1);

    assertTrue(result.isEmpty());
  }

  @Test
  void delete_shouldDoNothing_whenKeyDoesNotExist() {
    repository.delete(CHAT_ID_1);

    Optional<String> result = repository.findNodeKey(CHAT_ID_1);

    assertTrue(result.isEmpty());
  }
}
