package com.github.jawisimo.botengine.repository;

import com.github.jawisimo.botengine.config.BotProperties;
import com.github.jawisimo.botengine.interaction.node.model.UserState;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Facade that selects the appropriate {@link UserStateRepository} implementation at runtime.
 *
 * <p>Switches between {@link RedisUserStateRepository} and {@link InMemoryUserStateRepository}
 * based on {@code telegram.bot.user-state-persistent} property.
 *
 * @since 1.0
 */
@Repository
@RequiredArgsConstructor
public class UserStateRepositoryFacade {

  private final BotProperties properties;
  private RedisUserStateRepository redisUserStateRepository;
  private InMemoryUserStateRepository inMemoryUserStateRepository;

  /**
   * Injects {@link RedisUserStateRepository} only when persistent user state is enabled.
   *
   * <p>Optional Redis integration.
   *
   * <p>The bean is not required when {@code telegram.bot.user-state-persistent=false}.
   */
  @Autowired(required = false)
  private void setRedisUserStateRepository(RedisUserStateRepository redisUserStateRepository) {
    if (properties.userStatePersistent()) {
      this.redisUserStateRepository = redisUserStateRepository;
    }
  }

  /**
   * Injects {@link InMemoryUserStateRepository} when persistent user state is disabled.
   *
   * <p>Provides a fallback in-memory storage when Redis persistence is not used.
   */
  @Autowired(required = false)
  private void setInMemoryUserStateRepository(
      InMemoryUserStateRepository inMemoryUserStateRepository) {
    if (!properties.userStatePersistent()) {
      this.inMemoryUserStateRepository = inMemoryUserStateRepository;
    }
  }

  /**
   * Returns the stored user state for the given chat identifier.
   *
   * @param chatId the chat identifier
   * @return an {@link Optional} containing the user state, or empty if none exists
   */
  public Optional<UserState> findByChatId(String chatId) {
    if (properties.userStatePersistent()) {
      return redisUserStateRepository.findByChatId(chatId);
    }
    return inMemoryUserStateRepository.findByChatId(chatId);
  }

  /**
   * Persists the provided user state using the configured storage strategy.
   *
   * @param userState the user state to persist
   */
  public void save(UserState userState) {
    if (properties.userStatePersistent()) {
      redisUserStateRepository.save(userState);
    } else {
      inMemoryUserStateRepository.save(userState);
    }
  }
}
