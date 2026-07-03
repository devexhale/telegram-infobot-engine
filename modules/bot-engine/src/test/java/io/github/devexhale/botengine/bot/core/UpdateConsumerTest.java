package io.github.devexhale.botengine.bot.core;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import io.github.devexhale.botengine.execution.update.UpdateDispatcher;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Update;

@ExtendWith(MockitoExtension.class)
class UpdateConsumerTest {

  @Mock private UpdateDispatcher updateDispatcher;

  @Test
  void consume_shouldDispatchEachUpdate() {
    UpdateConsumer consumer = new UpdateConsumer(updateDispatcher);

    Update update1 = new Update();
    Update update2 = new Update();
    List<Update> updates = List.of(update1, update2);
    int expectedDispatchCalls = updates.size();
    Duration timeout = Duration.ofSeconds(1);

    consumer.consume(updates);

    ArgumentCaptor<Update> captor = ArgumentCaptor.forClass(Update.class);

    await()
        .atMost(timeout)
        .untilAsserted(
            () ->
                verify(updateDispatcher, times(expectedDispatchCalls)).dispatch(captor.capture()));

    assertEquals(updates, captor.getAllValues());
    verifyNoMoreInteractions(updateDispatcher);
  }

  @Test
  void consume_shouldDoNothing_whenUpdatesListIsEmpty() {
    UpdateConsumer consumer = new UpdateConsumer(updateDispatcher);

    consumer.consume(List.of());

    verifyNoInteractions(updateDispatcher);
  }
}
