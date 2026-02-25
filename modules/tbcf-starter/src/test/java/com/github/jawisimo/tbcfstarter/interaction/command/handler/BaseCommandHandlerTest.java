package com.github.jawisimo.tbcfstarter.interaction.command.handler;

import com.github.jawisimo.tbcfstarter.interaction.node.NodeExecutor;
import com.github.jawisimo.tbcfstarter.repository.DialogRepository;
import com.github.jawisimo.tbcfstarter.service.UserStateService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
abstract class BaseCommandHandlerTest {

  protected static final String CHAT_ID = "123456789";

  @Mock protected DialogRepository dialogRepository;
  @Mock protected UserStateService userStateService;
  @Mock protected NodeExecutor nodeExecutor;
}
