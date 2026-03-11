package io.github.jawisimo.botsengine.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import io.github.jawisimo.botsengine.service.UpdateService;
import java.util.List;
import java.util.concurrent.Executor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Update;

@ExtendWith(MockitoExtension.class)
class UpdateConsumerTest {

  @Mock private UpdateService updateService;

  @Test
  void consume_shouldDispatchEachUpdate_whenDirectExecutorIsUsed() {
    int updatesCount = 2;

    Executor directExecutor = Runnable::run;
    UpdateConsumer consumer = new UpdateConsumer(updateService, directExecutor);

    Update update1 = new Update();
    Update update2 = new Update();

    consumer.consume(List.of(update1, update2));

    ArgumentCaptor<Update> captor = ArgumentCaptor.forClass(Update.class);

    verify(updateService, times(updatesCount)).dispatch(captor.capture());
    assertEquals(List.of(update1, update2), captor.getAllValues());
    verifyNoMoreInteractions(updateService);
  }

  @Test
  void consume_shouldDoNothing_whenUpdatesListIsEmpty() {
    Executor directExecutor = Runnable::run;
    UpdateConsumer consumer = new UpdateConsumer(updateService, directExecutor);

    consumer.consume(List.of());

    verifyNoInteractions(updateService);
  }
}
