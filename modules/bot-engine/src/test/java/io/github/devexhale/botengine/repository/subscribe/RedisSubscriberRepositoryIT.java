package io.github.devexhale.botengine.repository.subscribe;

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
@ContextConfiguration(classes = {RedisSubscriberRepository.class})
class RedisSubscriberRepositoryIT {

  private static final String CHAT_ID_1 = "123456789";
  private static final String CHAT_ID_2 = "987654321";
  private static final String KEY = "bot:subscribers:all:";

  @Autowired private RedisSubscriberRepository repository;
  @Autowired private RedisTemplate<String, String> redisTemplate;

  @AfterEach
  void clearDb() {
    redisTemplate.delete(KEY);
  }

  @Test
  void save_shouldSaveSubscriber_whenCalled() {
    repository.save(CHAT_ID_1);

    Set<String> result = repository.getAll();

    assertTrue(result.contains(CHAT_ID_1));
    assertEquals(1, result.size());
  }

  @Test
  void save_shouldStoreMultipleSubscribers_whenCalledMultipleTimes() {
    repository.save(CHAT_ID_1);
    repository.save(CHAT_ID_2);

    Set<String> result = repository.getAll();

    assertEquals(2, result.size());
    assertTrue(result.contains(CHAT_ID_1));
    assertTrue(result.contains(CHAT_ID_2));
  }

  @Test
  void save_shouldIgnoreDuplicate_whenAddingExistingSubscriber() {
    repository.save(CHAT_ID_1);
    repository.save(CHAT_ID_1);

    Set<String> result = repository.getAll();

    assertEquals(1, result.size());
  }

  @Test
  void delete_shouldRemoveSubscriber_whenCalled() {
    repository.save(CHAT_ID_1);
    repository.save(CHAT_ID_2);
    repository.delete(CHAT_ID_1);

    Set<String> result = repository.getAll();

    assertEquals(1, result.size());
    assertTrue(result.contains(CHAT_ID_2));
    assertTrue(result.stream().noneMatch(id -> id.equals(CHAT_ID_1)));
  }

  @Test
  void delete_shouldDoNothing_whenSubscriberDoesNotExist() {
    repository.save(CHAT_ID_1);
    repository.delete("non_existent_chat");

    Set<String> result = repository.getAll();

    assertEquals(1, result.size());
    assertTrue(result.contains(CHAT_ID_1));
  }

  @Test
  void getAll_shouldReturnEmptySet_whenNoSubscribersExist() {
    Set<String> result = repository.getAll();

    assertTrue(result.isEmpty());
  }
}
