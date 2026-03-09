package com.github.jawisimo.botengine.config.appication;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * General application-level configuration for shared infrastructure components.
 *
 * <p>Defines common beans used across the bot-engine framework that are not directly related to
 * Telegram integration. These components provide runtime infrastructure required by various
 * framework services.
 *
 * <p>Currently provides a virtual thread executor used for concurrent task execution.
 *
 * @since 1.0
 */
@Configuration
public class ApplicationConfig {

  /**
   * Creates an {@link Executor} backed by Java virtual threads.
   *
   * <p>This executor creates a new virtual thread for each submitted task, allowing lightweight
   * concurrent processing without the overhead of platform threads.
   *
   * @return the configured virtual thread executor
   */
  @Bean
  public Executor virtualThreadsExecutor() {
    return Executors.newVirtualThreadPerTaskExecutor();
  }
}
