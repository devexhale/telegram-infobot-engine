package io.github.devexhale.botengine.diagnostics.analyzer;

import io.github.devexhale.botengine.diagnostics.exception.RedisInitializationException;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

public class RedisInitializationFailureAnalyzer
    extends AbstractFailureAnalyzer<RedisInitializationException> {

  @Override
  protected FailureAnalysis analyze(Throwable rootFailure, RedisInitializationException cause) {
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
