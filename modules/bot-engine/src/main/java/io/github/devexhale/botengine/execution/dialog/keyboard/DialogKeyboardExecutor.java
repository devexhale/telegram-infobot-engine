package io.github.devexhale.botengine.execution.dialog.keyboard;

import io.github.devexhale.botengine.diagnostics.exception.TelegramMessageSendException;
import io.github.devexhale.botengine.domain.dialog.Button;
import io.github.devexhale.botengine.domain.dialog.ButtonType;
import io.github.devexhale.botengine.domain.dialog.DialogNode;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Executes dialog keyboard markup sending for dialog nodes.
 *
 * <p>Builds and sends the message with configured inline or reply keyboard based on the dialog node
 * configuration.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DialogKeyboardExecutor {

  private final TelegramClient client;
  private final DialogKeyboardMarkupBuilder keyboardBuilder;

  /**
   * Sends the dialog message with configured keyboard markup.
   *
   * @param node the dialog node containing message and button configuration
   * @param chatId the target chat identifier
   * @return a list containing the sent Telegram {@link Message}
   * @throws TelegramMessageSendException if the Telegram API call fails
   */
  public List<Message> execute(DialogNode node, String chatId) {
    SendMessage request = createRequest(node, chatId);

    try {
      return List.of(client.execute(request));
    } catch (TelegramApiException e) {
      log.error("Failed to send dialog keyboard markup in chat. ChatID={}", chatId, e);
      throw new TelegramMessageSendException(e);
    }
  }

  private SendMessage createRequest(DialogNode node, String chatId) {
    List<Button> buttons = node.buttons();
    ButtonType buttonType = node.buttonType();

    SendMessage sendMessage = SendMessage.builder().chatId(chatId).text(node.message()).build();

    // DEFAULT = INLINE (if null)
    ButtonType effectiveType = buttonType != null ? buttonType : ButtonType.INLINE;

    switch (effectiveType) {
      case INLINE -> sendMessage.setReplyMarkup(keyboardBuilder.buildInlineKeyboard(buttons));
      case REPLY -> sendMessage.setReplyMarkup(keyboardBuilder.buildReplyKeyboard(buttons));
      default -> throw new IllegalStateException("Unsupported button type: " + effectiveType);
    }

    return sendMessage;
  }
}
