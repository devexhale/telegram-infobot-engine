package io.github.devexhale.botengine.repository.subscribe;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisSubscriberRepository implements SubscriberRepository {

  private static final String KEY = "bot:subscribers:all:";

  private final RedisTemplate<String, String> redisTemplate;

  @Override
  public void add(String chatId) {
    redisTemplate.opsForSet().add(KEY, chatId);
  }

  @Override
  public void delete(String chatId) {
    redisTemplate.opsForSet().remove(KEY, chatId);
  }

  @Override
  public Set<String> getAll() {
    Set<String> members = redisTemplate.opsForSet().members(KEY);
    return members != null ? members : Set.of();
  }
}
