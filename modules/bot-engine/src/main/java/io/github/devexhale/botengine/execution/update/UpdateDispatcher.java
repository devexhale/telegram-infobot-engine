package io.github.devexhale.botengine.execution.update;

import io.github.devexhale.botengine.execution.dialog.DialogExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Orchestrates processing of incoming Telegram updates within the framework.
 *
 * <p>Delegates supported update types to {@link DialogExecutor}. Message updates are routed to
 * {@code executeMessage}, callback query updates are routed to {@code executeCallback}.
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
   * Routes the update to appropriate dialog executor method.
   *
   * @param update the incoming Telegram update
   */
  public void dispatch(Update update) {
    Long chatId = null;

    try {
      if (update.hasMyChatMember()) {
        chatId = update.getMyChatMember().getChat().getId();
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
