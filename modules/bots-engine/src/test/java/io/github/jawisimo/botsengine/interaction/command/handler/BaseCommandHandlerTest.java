package io.github.jawisimo.botsengine.interaction.command.handler;

import io.github.jawisimo.botsengine.interaction.navigator.NodeExecutor;
import io.github.jawisimo.botsengine.repository.DialogRepository;
import io.github.jawisimo.botsengine.service.SubscriberService;
import io.github.jawisimo.botsengine.service.UserStateService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
abstract class BaseCommandHandlerTest {

  protected static final String CHAT_ID = "123456789";

  @Mock protected DialogRepository dialogRepository;
  @Mock protected SubscriberService subscriberService;
  @Mock protected UserStateService userStateService;
  @Mock protected NodeExecutor nodeExecutor;
}
