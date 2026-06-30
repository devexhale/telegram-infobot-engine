package io.github.devexhale.botengine.application;

import io.github.devexhale.botengine.storage.warm.DefinitionStorageWarmer;
import io.github.devexhale.botengine.validator.properties.PropertiesValidatorFacade;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Initializes core application components during startup.
 *
 * <p>Executes with the highest precedence to ensure all configurations are valid, caches are warmed
 * up, and Redis is available before the application starts processing requests.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class StartUpApplicationRunner {

  private final PropertiesValidatorFacade propertiesValidatorFacade;
  private final DefinitionStorageWarmer definitionStorageWarmer;
  private final RedisFailFastChecker redisFailFastChecker;

  /**
   * Performs startup initialization tasks.
   *
   * <p>Validates properties, warms up definition storage, and verifies Redis connectivity.
   */
  @PostConstruct
  public void init() {
    propertiesValidatorFacade.validateAll();
    definitionStorageWarmer.warmUpAll();
    redisFailFastChecker.check();
  }
}
