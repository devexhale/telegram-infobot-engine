package io.github.devexhale.botengine.repository.state;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisUserStateRepository implements UserStateRepository {

  private static final String KEY_PREFIX = "bot:user:state:";

  private final RedisTemplate<String, String> redisTemplate;

  @Override
  public Optional<String> findNodeKey(String chatId) {
    return Optional.ofNullable(redisTemplate.opsForValue().get(KEY_PREFIX + chatId));
  }

  @Override
  public void save(String chatId, String nodeId) {
    redisTemplate.opsForValue().set(KEY_PREFIX + chatId, nodeId);
  }

  @Override
  public void delete(String chatId) {
    redisTemplate.delete(KEY_PREFIX + chatId);
  }
}
