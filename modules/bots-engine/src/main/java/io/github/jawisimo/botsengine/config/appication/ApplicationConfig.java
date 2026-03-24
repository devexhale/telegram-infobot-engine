package io.github.jawisimo.botsengine.config.appication;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;

/**
 * General application-level configuration for shared infrastructure components.
 *
 * <p>Defines common beans used across the bots-engine framework that are not directly related to
 * Telegram integration. These components provide runtime infrastructure required by various
 * framework services.
 *
 * <p>Currently provides a virtual thread executor used for concurrent task execution.
 *
 * @since 1.0
 */
@Configuration
@EnableScheduling
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

  @Bean
  public TaskScheduler virtualThreadsTaskScheduler() {
    SimpleAsyncTaskScheduler scheduler = new SimpleAsyncTaskScheduler();
    scheduler.setVirtualThreads(true);
    scheduler.setThreadNamePrefix("scheduler-");
    return scheduler;
  }
}
