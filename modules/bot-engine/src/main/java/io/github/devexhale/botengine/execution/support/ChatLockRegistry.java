package io.github.devexhale.botengine.execution.support;

import com.google.common.util.concurrent.Striped;
import java.util.concurrent.locks.Lock;

import lombok.NonNull;
import org.springframework.stereotype.Component;

/**
 * Provides per-chat locks to serialize dialog and broadcast message delivery for the same chat,
 * preventing interleaved output when both execute concurrently.
 *
 * <p>Uses a fixed-size striped lock pool to avoid unbounded memory growth as new chat IDs are seen,
 * at the cost of rare, harmless lock contention between unrelated chats that happen to hash to the
 * same stripe.
 *
 * @since 1.0
 */
@Component
public class ChatLockRegistry {

  private static final int STRIPE_COUNT = 1024;

  private final Striped<@NonNull Lock> locks = Striped.lock(STRIPE_COUNT);

  /**
   * Returns the lock associated with the given chat ID.
   *
   * @param chatId the chat identifier
   * @return the lock to synchronize on for this chat
   */
  public Lock getLock(String chatId) {
    return locks.get(chatId);
  }
}
