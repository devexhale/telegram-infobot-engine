package io.github.devexhale.botengine.application;

import static io.github.devexhale.botengine.util.EnvironmentDetector.isTestEnvironment;

import io.github.devexhale.botengine.storage.warm.DefinitionStorageWarmer;
import io.github.devexhale.botengine.validator.properties.PropertiesValidatorFacade;
import jakarta.annotation.PostConstruct;

/**
 * Initializes core application components during startup.
 *
 * <p>Ensure all configurations are valid, caches are warmed up, and Redis is available before the
 * application starts processing requests.
 *
 * @since 1.0
 */
public record StartUpApplicationRunner(
    PropertiesValidatorFacade propertiesValidatorFacade,
    DefinitionStorageWarmer definitionStorageWarmer,
    RedisFailFastChecker redisFailFastChecker) {

  /**
   * Performs startup initialization tasks.
   *
   * <p>Validates properties, warms up definition storage, and verifies Redis connectivity. Skip the
   * operations if the application is running in a test environment.
   */
  @PostConstruct
  public void init() {
    if (isTestEnvironment()) {
      return;
    }

    propertiesValidatorFacade.validateAll();
    definitionStorageWarmer.warmUpAll();
    redisFailFastChecker.check();
  }
}
