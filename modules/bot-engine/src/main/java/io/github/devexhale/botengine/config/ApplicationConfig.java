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
 * Defines general application-level bean configurations and core infrastructure.
 *
 * @since 1.0
 */
@Configuration
@EnableScheduling
public class ApplicationConfig {

  private static final int SCHEDULED_POOL_SIZE = 4;
  private static final int EXECUTOR_POOL_START_SUFFIX = 1;

  /**
   * Creates a JSON {@link ObjectMapper} configured with Java time support.
   *
   * @return the configured JSON object mapper
   */
  @Bean
  public ObjectMapper jsonMapper() {
    return new ObjectMapper().registerModule(new JavaTimeModule());
  }

  /**
   * Creates a YAML {@link ObjectMapper} configured with Java time support.
   *
   * @return the configured YAML object mapper
   */
  @Bean
  public ObjectMapper yamlMapper() {
    return new ObjectMapper(new YAMLFactory()).registerModule(new JavaTimeModule());
  }

  /**
   * Creates a primary {@link Executor} that spawns a new virtual thread for each task.
   *
   * @return the virtual thread executor
   */
  @Bean
  @Primary
  public Executor virtualThreadsExecutor() {
    return Executors.newVirtualThreadPerTaskExecutor();
  }

  /**
   * Creates a {@link ScheduledExecutorService} with a fixed pool of daemon threads.
   *
   * @return the scheduled executor service
   */
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
