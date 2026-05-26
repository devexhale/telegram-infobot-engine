package io.github.devexhale.botengine.bot.rate;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.devexhale.botengine.properties.BotProperties;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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

  public void acquire(String chatId) {
    globalAcquire();
    chatAcquire(chatId);
  }

  private void globalAcquire() {
    proxyManager
        .builder()
        .build(GLOBAL_KEY, () -> globalConfig)
        .asBlocking()
        .consumeUninterruptibly(CONSUME_NUM_TOKENS);
  }

  private void chatAcquire(String chatId) {
    if (chatId != null) {
      proxyManager
          .builder()
          .build(CHAT_KEY_PREFIX + chatId, () -> chatConfig)
          .asBlocking()
          .consumeUninterruptibly(CONSUME_NUM_TOKENS);
    }
  }

  private BucketConfiguration buildConfig(int capacity, int rate, Duration interval) {
    return BucketConfiguration.builder()
        .addLimit(Bandwidth.builder().capacity(capacity).refillGreedy(rate, interval).build())
        .build();
  }

  public long getAvailableGlobalTokens() {
    return proxyManager.builder().build(GLOBAL_KEY, () -> globalConfig).getAvailableTokens();
  }
}
