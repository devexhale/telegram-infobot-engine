package io.github.devexhale.botengine.config;

import io.github.devexhale.botengine.application.RedisFailFastChecker;
import io.github.devexhale.botengine.application.StartUpApplicationRunner;
import io.github.devexhale.botengine.storage.warm.DefinitionStorageWarmer;
import io.github.devexhale.botengine.validator.properties.PropertiesValidatorFacade;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Defines general application-level bean configurations and core infrastructure.
 *
 * @since 1.0
 */
@Configuration
public class ApplicationConfig {

  private static final int SCHEDULED_POOL_SIZE = 4;
  private static final int SCHEDULED_POOL_START_SUFFIX = 1;

  /** Initializes core application components during startup. */
  @Bean
  public StartUpApplicationRunner startUpApplicationRunner(
      PropertiesValidatorFacade propertiesValidatorFacade,
      DefinitionStorageWarmer definitionStorageWarmer,
      RedisFailFastChecker redisFailFastChecker) {
    return new StartUpApplicationRunner(
        propertiesValidatorFacade, definitionStorageWarmer, redisFailFastChecker);
  }

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
