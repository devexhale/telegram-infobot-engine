package io.github.devexhale.botengine.bot.rate;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.devexhale.botengine.properties.BotProperties;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Manages rate limiting for Telegram API requests using Bucket4J.
 *
 * <p>Enforces both global and per-chat rate limits based on configured capacity and refill rates.
 * Uses a distributed proxy manager for state management across instances.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class RateLimiter {

  private static final int CONSUME_NUM_TOKENS = 1;
  private static final String GLOBAL_KEY = "bot:rate:global";
  private static final String CHAT_KEY_PREFIX = "bot:rate:chat:";

  private final BotProperties botProperties;
  private final ProxyManager<String> proxyManager;
  private BucketConfiguration globalConfig;
  private BucketConfiguration chatConfig;

  /** Initializes bucket configurations for global and chat-level rate limits. */
  @PostConstruct
  public void init() {
    globalConfig =
        buildConfig(
            botProperties.rateLimit().global().capacity(),
            botProperties.rateLimit().global().rate(),
            botProperties.rateLimit().global().interval());
    chatConfig =
        buildConfig(
            botProperties.rateLimit().chat().capacity(),
            botProperties.rateLimit().chat().rate(),
            botProperties.rateLimit().chat().interval());
  }

  /**
   * Acquires tokens for both global and chat-specific rate limits.
   *
   * @param chatId the chat ID for per-chat rate limiting, may be null
   */
  public void acquire(String chatId) {
    globalAcquire();
    chatAcquire(chatId);
  }

  /** Acquires a token from the global rate limit bucket. */
  private void globalAcquire() {
    proxyManager
        .builder()
        .build(GLOBAL_KEY, () -> globalConfig)
        .asBlocking()
        .consumeUninterruptibly(CONSUME_NUM_TOKENS);
  }

  /**
   * Acquires a token from the chat-specific rate limit bucket.
   *
   * @param chatId the chat ID, if null the acquisition is skipped
   */
  private void chatAcquire(String chatId) {
    if (chatId != null) {
      proxyManager
          .builder()
          .build(CHAT_KEY_PREFIX + chatId, () -> chatConfig)
          .asBlocking()
          .consumeUninterruptibly(CONSUME_NUM_TOKENS);
    }
  }

  /**
   * Builds a bucket configuration with the specified capacity and refill rate.
   *
   * @param capacity the maximum number of tokens
   * @param rate the number of tokens to refill
   * @param interval the refill interval
   * @return the configured bucket
   */
  private BucketConfiguration buildConfig(int capacity, int rate, Duration interval) {
    return BucketConfiguration.builder()
        .addLimit(Bandwidth.builder().capacity(capacity).refillGreedy(rate, interval).build())
        .build();
  }
}
