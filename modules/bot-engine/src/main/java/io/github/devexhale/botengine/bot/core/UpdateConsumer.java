package io.github.devexhale.botengine.bot.core;

import io.github.devexhale.botengine.execution.update.UpdateDispatcher;

import java.util.List;
import java.util.concurrent.Executor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Consumes incoming Telegram updates and delegates them to the framework processing pipeline.
 *
 * <p>Acts as a bridge between the Telegram long polling infrastructure and the bots-engine
 * framework. Receives batches of updates from Telegram and forwards them to {@link
 * UpdateDispatcher}, which orchestrates dialog execution.
 *
 * <p>Updates are dispatched asynchronously using the {@code virtualThreadsExecutor}, allowing
 * concurrent processing of update batches. Each update from the received batch is submitted as an
 * independent task, allowing concurrent processing across multiple virtual threads. This enables
 * high throughput while keeping the polling consumer lightweight and responsive.
 *
 * <p>Execution flow: TelegramBot -> UpdateConsumer -> UpdateDispatcher -> dialog execution
 * pipeline.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateConsumer implements LongPollingUpdateConsumer {

  private final Executor virtualThreadsExecutor;
  private final UpdateDispatcher updateDispatcher;

  /**
   * Consumes a batch of Telegram {@link Update updates} and submits them for asynchronous
   * processing.
   *
   * <p>Each update is dispatched to {@link UpdateDispatcher#dispatch(Update)} using the configured
   * {@code virtualThreadsExecutor}. This allows updates to be processed concurrently while the
   * consumer remains free to accept new batches from the Telegram long polling mechanism.
   *
   * @param updates the list of incoming Telegram updates received from the polling session
   */
  @Override
  public void consume(List<Update> updates) {
    for (Update update : updates) {
      virtualThreadsExecutor.execute(() -> updateDispatcher.dispatch(update));
    }
  }
}
