package io.github.devexhale.botengine.repository.message;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.devexhale.botengine.testsupport.container.WithRedisTestContainer;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;

@WithRedisTestContainer
@ContextConfiguration(classes = {RedisMessageCleanupRepository.class})
class RedisMessageCleanupRepositoryIT {

  private static final String CHAT_ID_1 = "123456789";
  private static final String CHAT_ID_2 = "987654321";
  private static final Integer MESSAGE_ID_1 = 100;
  private static final Integer MESSAGE_ID_2 = 101;
  private static final String KEY_PREFIX = "bot:user:messages:cleanup:";

  @Autowired private RedisMessageCleanupRepository repository;
  @Autowired private RedisTemplate<String, String> redisTemplate;

  @AfterEach
  void clearDb() {
    Set<String> keys = redisTemplate.keys(KEY_PREFIX + "*");

    if (!keys.isEmpty()) {
      redisTemplate.delete(keys);
    }
  }

  @Test
  void save_shouldAddMessageIdToSet_whenCalled() {
    int expectedSize = 1;

    repository.save(CHAT_ID_1, MESSAGE_ID_1);

    Set<Integer> result = repository.deleteAllByChatId(CHAT_ID_1);

    assertTrue(result.contains(MESSAGE_ID_1));
    assertEquals(expectedSize, result.size());
  }

  @Test
  void save_shouldStoreMultipleIdsForSameChat_whenCalledMultipleTimes() {
    int expectedSize = 2;

    repository.save(CHAT_ID_1, MESSAGE_ID_1);
    repository.save(CHAT_ID_1, MESSAGE_ID_2);

    Set<Integer> result = repository.deleteAllByChatId(CHAT_ID_1);

    assertEquals(expectedSize, result.size());
    assertTrue(result.contains(MESSAGE_ID_1));
    assertTrue(result.contains(MESSAGE_ID_2));
  }

  @Test
  void save_shouldIsolateDataBetweenDifferentChats_whenCalled() {
    int expectedSize = 1;

    repository.save(CHAT_ID_1, MESSAGE_ID_1);
    repository.save(CHAT_ID_2, MESSAGE_ID_2);

    Set<Integer> result1 = repository.deleteAllByChatId(CHAT_ID_1);
    Set<Integer> result2 = repository.deleteAllByChatId(CHAT_ID_2);

    assertEquals(expectedSize, result1.size());
    assertTrue(result1.contains(MESSAGE_ID_1));

    assertEquals(expectedSize, result2.size());
    assertTrue(result2.contains(MESSAGE_ID_2));
  }

  @Test
  void deleteAllByChatId_shouldReturnIdsAndDeleteKey_whenMessagesExist() {
    int expectedSize = 2;

    repository.save(CHAT_ID_1, MESSAGE_ID_1);
    repository.save(CHAT_ID_1, MESSAGE_ID_2);

    Set<Integer> result = repository.deleteAllByChatId(CHAT_ID_1);

    assertEquals(expectedSize, result.size());
    assertTrue(result.contains(MESSAGE_ID_1));
    assertTrue(result.contains(MESSAGE_ID_2));

    Set<Integer> emptyResult = repository.deleteAllByChatId(CHAT_ID_1);

    assertTrue(emptyResult.isEmpty());
  }

  @Test
  void deleteAllByChatId_shouldReturnEmptySet_whenChatIdHasNoMessages() {
    String nonExistentChatId = "non_existent_chat";

    Set<Integer> result = repository.deleteAllByChatId(nonExistentChatId);

    assertTrue(result.isEmpty());
  }
}
