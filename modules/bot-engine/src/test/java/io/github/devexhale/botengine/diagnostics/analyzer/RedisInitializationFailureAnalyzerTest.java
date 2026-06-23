package io.github.devexhale.botengine.diagnostics.analyzer;

import io.github.devexhale.botengine.diagnostics.exception.RedisInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.diagnostics.FailureAnalysis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RedisInitializationFailureAnalyzerTest {

  private final RedisInitializationFailureAnalyzer analyzer =
      new RedisInitializationFailureAnalyzer();

  @Test
  void analyze_shouldReturnCorrectFailureAnalysis_whenExceptionProvided() {
    String exceptionMsg = "Connection to Redis failed. Please check your connection.";
    RedisInitializationException cause = new RedisInitializationException(exceptionMsg);

    FailureAnalysis analysis = analyzer.analyze(cause, cause);

    String expectedAction =
        """
        Ensure that:
        • spring-boot-starter-data-redis dependency is added to pom.xml or build.gradle
        • Redis server is running on the configured host/port
        • spring.data.redis.host and spring.data.redis.port are correct
        • Firewall / network / Docker allows connection to redis port
        • Redis is not in protected mode or requires password (if configured)
        """;

    assertNotNull(analysis);
    assertEquals(exceptionMsg, analysis.getDescription());
    assertEquals(expectedAction, analysis.getAction());
    assertEquals(cause, analysis.getCause());
  }
}
