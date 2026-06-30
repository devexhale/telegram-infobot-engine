package io.github.devexhale.botengine.properties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Configuration properties for the Telegram bot.
 *
 * @param token the bot authentication token
 * @param name the bot display name
 * @param rateLimit the rate limiting configuration
 * @since 1.0
 */
@ConfigurationProperties(prefix = "telegram.bot")
@Slf4j
public record BotProperties(String token, String name, RateLimit rateLimit) {

  /**
   * Rate limiting configuration containing global and per-chat limits.
   *
   * @param global the global rate limit settings
   * @param chat the per-chat rate limit settings
   */
  public record RateLimit(Global global, Chat chat) {

    /**
     * Global rate limit settings applied across all chats.
     *
     * @param capacity the maximum number of tokens
     * @param rate the number of tokens to refill
     * @param interval the refill interval
     */
    public record Global(int capacity, int rate, Duration interval) {}

    /**
     * Per-chat rate limit settings applied to individual chats.
     *
     * @param capacity the maximum number of tokens
     * @param rate the number of tokens to refill
     * @param interval the refill interval
     */
    public record Chat(int capacity, int rate, Duration interval) {}
  }
}
