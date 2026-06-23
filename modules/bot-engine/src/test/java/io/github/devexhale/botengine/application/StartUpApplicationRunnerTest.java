package io.github.devexhale.botengine.application;

import static org.mockito.Mockito.inOrder;

import io.github.devexhale.botengine.storage.warm.DefinitionStorageWarmer;
import io.github.devexhale.botengine.validator.properties.PropertiesValidatorFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StartUpApplicationRunnerTest {

  @Mock private PropertiesValidatorFacade propertiesValidatorFacade;

  @Mock private DefinitionStorageWarmer definitionStorageWarmer;

  @Mock private RedisFailFastChecker redisFailFastChecker;

  @InjectMocks private StartUpApplicationRunner startUpApplicationRunner;

  @Test
  void init_shouldExecuteAllStepsInCorrectOrder_whenCalled() {
    startUpApplicationRunner.init();

    InOrder inOrder =
        inOrder(propertiesValidatorFacade, definitionStorageWarmer, redisFailFastChecker);

    inOrder.verify(propertiesValidatorFacade).validateAll();
    inOrder.verify(definitionStorageWarmer).warmUpAll();
    inOrder.verify(redisFailFastChecker).check();
  }
}
