package io.github.jawisimo.botsengine.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class SubscriberRedisRepository implements SubscriberRepository {

  private static final String KEY = "bot:subscribers:all";

  private final RedisTemplate<String, String> redisTemplate;

  @Override
  public void add(String chatId) {
    redisTemplate.opsForSet().add(KEY, chatId);
  }

  @Override
  public void remove(String chatId) {
    redisTemplate.opsForSet().remove(KEY, chatId);
  }

  @Override
  public Set<String> getAll() {
    Set<String> members = redisTemplate.opsForSet().members(KEY);
    return members != null ? members : Set.of();
  }
}
