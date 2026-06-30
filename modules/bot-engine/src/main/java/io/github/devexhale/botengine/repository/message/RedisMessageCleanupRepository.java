package io.github.devexhale.botengine.repository.message;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * Redis-backed implementation of {@link MessageCleanupRepository}.
 *
 * <p>Manages message identifiers in Redis for efficient cleanup operations.
 *
 * @since 1.0
 */
@Repository
@RequiredArgsConstructor
public class RedisMessageCleanupRepository implements MessageCleanupRepository {

  private static final String KEY_PREFIX = "bot:user:messages:cleanup:";

  private final RedisTemplate<String, String> redisTemplate;

  @Override
  public void save(String chatId, Integer messageId) {
    redisTemplate.opsForSet().add(KEY_PREFIX + chatId, messageId.toString());
  }

  @Override
  public Set<Integer> deleteAllByChatId(String chatId) {
    Set<String> stringIds = redisTemplate.opsForSet().members(KEY_PREFIX + chatId);
    redisTemplate.delete(KEY_PREFIX + chatId);

    if (stringIds == null || stringIds.isEmpty()) {
      return Collections.emptySet();
    }

    return stringIds.stream().map(Integer::valueOf).collect(Collectors.toSet());
  }
}
