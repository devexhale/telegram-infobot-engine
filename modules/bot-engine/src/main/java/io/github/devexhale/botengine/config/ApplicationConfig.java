package io.github.devexhale.botengine.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;

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

  private static final int SCHEDULED_POOL_SIZE = 4;
  private static final int EXECUTOR_POOL_START_SUFFIX = 1;

  @Bean
  public ObjectMapper jsonMapper() {
    return new ObjectMapper().registerModule(new JavaTimeModule());
  }

  @Bean
  public ObjectMapper yamlMapper() {
    return new ObjectMapper(new YAMLFactory()).registerModule(new JavaTimeModule());
  }

  /**
   * Creates an {@link Executor} backed by Java virtual threads.
   *
   * <p>This executor creates a new virtual thread for each submitted task, allowing lightweight
   * concurrent processing without the overhead of platform threads.
   *
   * @return the configured virtual thread executor
   */
  @Bean
  @Primary
  public Executor virtualThreadsExecutor() {
    return Executors.newVirtualThreadPerTaskExecutor();
  }

  @Bean(destroyMethod = "shutdown")
  public ScheduledExecutorService scheduledExecutorService() {
    return Executors.newScheduledThreadPool(
        SCHEDULED_POOL_SIZE,
        Thread.ofPlatform()
            .name("scheduled-executor-", EXECUTOR_POOL_START_SUFFIX)
            .daemon(true)
            .factory());
  }
}
