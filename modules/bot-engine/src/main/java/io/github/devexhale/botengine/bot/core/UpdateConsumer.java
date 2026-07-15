package io.github.devexhale.botengine.bot.core;

import io.github.devexhale.botengine.execution.update.UpdateDispatcher;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Consumes incoming Telegram updates and dispatches them asynchronously.
 *
 * <p>Delegates each {@link Update} to the {@link UpdateDispatcher} using a virtual thread executor
 * for concurrent processing.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateConsumer implements LongPollingUpdateConsumer {

  private final ExecutorService virtualThreadsExecutor =
      Executors.newVirtualThreadPerTaskExecutor();
  private final UpdateDispatcher updateDispatcher;

  /**
   * Processes a batch of updates by submitting each to the virtual thread executor.
   *
   * @param updates the list of incoming {@link Update} objects
   */
  @Override
  public void consume(List<Update> updates) {
    for (Update update : updates) {
      virtualThreadsExecutor.execute(() -> updateDispatcher.dispatch(update));
    }
  }
}
