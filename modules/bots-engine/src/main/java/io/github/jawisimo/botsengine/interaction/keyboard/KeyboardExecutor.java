package io.github.jawisimo.botsengine.interaction.keyboard;

import io.github.jawisimo.botsengine.model.Button;
import io.github.jawisimo.botsengine.model.ButtonType;
import io.github.jawisimo.botsengine.model.DialogNode;
import io.github.jawisimo.botsengine.repository.MessageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Executes keyboard rendering for a dialog node.
 *
 * <p>Builds appropriate keyboard markup, sends the message via {@link TelegramClient}, and stores
 * the sent message identifier for later cleanup.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KeyboardExecutor {

  private final TelegramClient client;
  private final KeyboardMarkupBuilder keyboardBuilder;
  private final MessageRepository messageRepository;

  /**
   * Sends a dialog message with keyboard markup for the specified chat.
   *
   * @param node the dialog node containing button configuration
   * @param chatId the chat identifier
   */
  public void execute(DialogNode node, String chatId) {
    List<Button> buttons = node.buttons();
    ButtonType buttonType = node.buttonType();

    SendMessage sendMessage = SendMessage.builder().chatId(chatId).text(node.message()).build();

    switch (buttonType) {
      case ButtonType.INLINE ->
          sendMessage.setReplyMarkup(keyboardBuilder.buildInlineKeyboard(buttons));
      case ButtonType.REPLY ->
          sendMessage.setReplyMarkup(keyboardBuilder.buildReplyKeyboard(buttons));
      default -> {
        return;
      }
    }

    try {
      Message sent = client.execute(sendMessage);
      messageRepository.save(chatId, sent.getMessageId());
    } catch (TelegramApiException e) {
      log.error("Failed to execute keyboard markup in chat: {}", chatId, e);
    }
  }
}
