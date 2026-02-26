package com.github.jawisimo.botengine.core;

import com.github.jawisimo.botengine.interaction.command.CommandsInitializer;
import com.github.jawisimo.botengine.service.UpdateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateConsumerTest {

  @Mock private CommandsInitializer commandsInitializer;
  @Mock private UpdateService updateService;

  @Test
  void init_shouldSetUpCommands_whenCalled() {
    UpdateConsumer consumer = new UpdateConsumer(commandsInitializer, updateService);

    consumer.init();

    verify(commandsInitializer).setUpCommands();
    verifyNoInteractions(updateService);
  }

  @Test
  void consume_shouldDelegateToUpdateService_whenInvoked() {
    UpdateConsumer consumer = new UpdateConsumer(commandsInitializer, updateService);

    Update update = new Update();

    consumer.consume(update);

    verify(updateService).onUpdateReceived(update);
    verifyNoInteractions(commandsInitializer);
  }
}
