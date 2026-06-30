package io.github.devexhale.botengine.execution.update;

import io.github.devexhale.botengine.execution.dialog.DialogExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Orchestrates the processing of incoming Telegram updates.
 *
 * <p>Delegates messages and callback queries to {@link DialogExecutor}, and chat member updates to
 * {@link ChatMemberUpdateExecutor}.
 *
 * @since 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateDispatcher {

  private final DialogExecutor dialogExecutor;
  private final ChatMemberUpdateExecutor chatMemberUpdateExecutor;

  /**
   * Routes the incoming update to the appropriate handler.
   *
   * @param update the incoming Telegram {@link Update}
   */
  public void dispatch(Update update) {
    Long chatId = null;

    try {
      if (update.hasMyChatMember()) {
        chatMemberUpdateExecutor.execute(update.getMyChatMember());
        return;
      }

      if (update.hasMessage()) {
        chatId = update.getMessage().getChatId();
        dialogExecutor.executeMessage(update.getMessage());
      } else if (update.hasCallbackQuery()) {
        chatId = update.getCallbackQuery().getMessage().getChatId();
        dialogExecutor.executeCallback(update.getCallbackQuery());
      } else {
        log.warn("Unsupported update type: {}", update);
      }
    } catch (Exception e) {
      log.error(
          "An error occurred during update processing. UpdateID={} ; ChatID={}",
          update.getUpdateId(),
          chatId,
          e);
    }
  }
}
