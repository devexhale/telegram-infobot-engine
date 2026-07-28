package io.github.devexhale.botengine.diagnostics.analyzer;

import io.github.devexhale.botengine.diagnostics.exception.RedisInitializationException;
import lombok.NonNull;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/**
 * Analyzes {@link RedisInitializationException} to provide user-friendly diagnostics when Redis
 * connection or initialization fails.
 *
 * @since 1.0
 */
public class RedisInitializationFailureAnalyzer
    extends AbstractFailureAnalyzer<@NonNull RedisInitializationException> {

  @Override
  protected FailureAnalysis analyze(
      @NonNull Throwable rootFailure, RedisInitializationException cause) {
    String action =
        """
        Ensure that:
        • spring-boot-starter-data-redis dependency is added to pom.xml or build.gradle
        • Redis server is running on the configured host/port
        • spring.data.redis.host and spring.data.redis.port are correct
        • Firewall / network / Docker allows connection to redis port
        • Redis is not in protected mode or requires password (if configured)
        """;

    return new FailureAnalysis(cause.getMessage(), action, cause);
  }
}
