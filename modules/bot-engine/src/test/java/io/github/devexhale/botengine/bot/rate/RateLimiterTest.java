package io.github.devexhale.botengine.bot.rate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.devexhale.botengine.properties.BotProperties;
import io.github.devexhale.botengine.properties.BotProperties.RateLimit;
import java.time.Duration;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RateLimiterTest {

  private static final String CHAT_ID = "123456789";
  private static final int GLOBAL_CAPACITY = 100;
  private static final int GLOBAL_RATE = 10;
  private static final Duration GLOBAL_INTERVAL = Duration.ofSeconds(1);
  private static final int CHAT_CAPACITY = 50;
  private static final int CHAT_RATE = 5;
  private static final Duration CHAT_INTERVAL = Duration.ofSeconds(1);

  @Mock private BotProperties botProperties;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private ProxyManager<String> proxyManager;

  private RateLimiter rateLimiter;

  @BeforeEach
  void setUp() {
    RateLimit.Global global = new RateLimit.Global(GLOBAL_CAPACITY, GLOBAL_RATE, GLOBAL_INTERVAL);
    RateLimit.Chat chat = new RateLimit.Chat(CHAT_CAPACITY, CHAT_RATE, CHAT_INTERVAL);
    RateLimit rateLimit = new RateLimit(global, chat);

    lenient().when(botProperties.rateLimit()).thenReturn(rateLimit);

    rateLimiter = new RateLimiter(botProperties, proxyManager);
    rateLimiter.init();
  }

  @Test
  void acquire_shouldConsumeTokensFromGlobalAndChatBuckets_whenChatIdIsProvided() {
    int expectedBucketCount = 2;

    rateLimiter.acquire(CHAT_ID);

    verify(proxyManager.builder(), times(expectedBucketCount))
        .build(anyString(), ArgumentMatchers.<Supplier<BucketConfiguration>>any());
  }

  @Test
  void acquire_shouldConsumeTokensOnlyFromGlobalBucket_whenChatIdIsNull() {
    rateLimiter.acquire(null);

    verify(proxyManager.builder())
        .build(anyString(), ArgumentMatchers.<Supplier<BucketConfiguration>>any());
  }
}
