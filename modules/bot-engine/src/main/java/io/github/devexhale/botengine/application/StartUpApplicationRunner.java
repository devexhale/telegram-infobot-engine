package io.github.devexhale.botengine.application;

import io.github.devexhale.botengine.storage.warm.DefinitionStorageWarmer;
import io.github.devexhale.botengine.validator.properties.PropertiesValidatorFacade;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class StartUpApplicationRunner {

  private final PropertiesValidatorFacade propertiesValidatorFacade;
  private final DefinitionStorageWarmer definitionStorageWarmer;
  private final RedisFailFastChecker redisFailFastChecker;

  @PostConstruct
  public void init() {
    propertiesValidatorFacade.validateAll();
    definitionStorageWarmer.warmUpAll();
    redisFailFastChecker.check();
  }
}
