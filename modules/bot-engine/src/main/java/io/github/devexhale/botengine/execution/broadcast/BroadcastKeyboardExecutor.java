package io.github.devexhale.botengine.execution.broadcast;

import io.github.devexhale.botengine.annotation.ConditionalOnBroadcastEnabled;
import io.github.devexhale.botengine.diagnostics.exception.TelegramMessageSendException;
import io.github.devexhale.botengine.domain.broadcast.BroadcastNode;
import java.util.List;

import io.github.devexhale.botengine.execution.common.command.commandset.LastCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Sends an inline keyboard with a return button for broadcast messages.
 *
 * <p>The return button navigates the user back to the main dialog by triggering the {@code /last}
 * command.
 *
 * @since 1.0
 */
@Component
@ConditionalOnBroadcastEnabled
@RequiredArgsConstructor
@Slf4j
public class BroadcastKeyboardExecutor {

  private final TelegramClient client;

  /**
   * Sends the broadcast message with an inline return button to the specified chat.
   *
   * @param node the broadcast node containing the message and button label
   * @param chatId the target chat ID
   * @throws TelegramMessageSendException if the Telegram API call fails
   */
  public void execute(BroadcastNode node, String chatId) {
    InlineKeyboardMarkup keyboard = createReturnButtonKeyboardMarkup(node);

    SendMessage request =
        SendMessage.builder().chatId(chatId).text(node.message()).replyMarkup(keyboard).build();

    try {
      client.execute(request);
    } catch (TelegramApiException e) {
      log.error("Failed to send broadcast keyboard markup in chat. ChatID={}", chatId);
      throw new TelegramMessageSendException(e);
    }
  }

  private InlineKeyboardMarkup createReturnButtonKeyboardMarkup(BroadcastNode node) {
    InlineKeyboardButton button =
        InlineKeyboardButton.builder()
            .text(node.returnButtonLabel())
            .callbackData(LastCommand.COMMAND_NAME)
            .build();

    return InlineKeyboardMarkup.builder().keyboard(List.of(new InlineKeyboardRow(button))).build();
  }
}
