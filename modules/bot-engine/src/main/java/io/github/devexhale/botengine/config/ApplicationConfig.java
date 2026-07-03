package io.github.devexhale.botengine.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Defines general application-level bean configurations and core infrastructure.
 *
 * @since 1.0
 */
@Configuration
@EnableScheduling
public class ApplicationConfig {

  private static final int SCHEDULED_POOL_SIZE = 4;
  private static final int SCHEDULED_POOL_START_SUFFIX = 1;

  /**
   * Creates a {@link Executor} that spawns a new virtual thread for each task.
   *
   * @return the virtual thread executor
   */
  @Bean(destroyMethod = "shutdownNow")
  public ExecutorService botEngineVirtualThreadsExecutor() {
    return Executors.newVirtualThreadPerTaskExecutor();
  }

  /**
   * Creates a {@link ScheduledExecutorService} with a fixed pool of daemon threads.
   *
   * @return the scheduled executor service
   */
  @Bean(destroyMethod = "shutdownNow")
  public ScheduledExecutorService botEngineScheduledExecutorService() {
    return Executors.newScheduledThreadPool(
        SCHEDULED_POOL_SIZE,
        Thread.ofPlatform()
            .name("scheduled-executor-", SCHEDULED_POOL_START_SUFFIX)
            .daemon(true)
            .factory());
  }
}
