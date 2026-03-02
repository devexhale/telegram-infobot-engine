package com.github.jawisimo.botengine.interaction.keyboard;

import com.github.jawisimo.botengine.interaction.node.model.Button;
import com.github.jawisimo.botengine.interaction.node.model.ButtonType;
import com.github.jawisimo.botengine.interaction.node.model.DialogNode;
import com.github.jawisimo.botengine.repository.MessageRepository;
import com.github.jawisimo.botengine.validator.DialogValidator;
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
 * <p>Validates button configuration, builds appropriate keyboard markup, sends the message via
 * {@link TelegramClient}, and stores the sent message identifier for later cleanup.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KeyboardExecutor {

  private final TelegramClient client;
  private final KeyboardMarkupBuilder keyboardBuilder;
  private final DialogValidator dialogValidator;
  private final MessageRepository messageRepository;

  /**
   * Sends a dialog message with keyboard markup for the specified chat.
   *
   * @param node the dialog node containing button configuration
   * @param chatId the chat identifier
   */
  public void execute(DialogNode node, String chatId) {
    List<Button> buttons = node.buttons();
    dialogValidator.validateButtons(node);

    SendMessage sendMessage = SendMessage.builder().chatId(chatId).text(node.message()).build();

    if (node.buttonType() == ButtonType.REPLY) {
      sendMessage.setReplyMarkup(keyboardBuilder.buildReplyKeyboard(buttons));
    } else {
      sendMessage.setReplyMarkup(keyboardBuilder.buildInlineKeyboard(buttons));
    }

    try {
      Message sent = client.execute(sendMessage);
      messageRepository.save(chatId, sent.getMessageId());
    } catch (TelegramApiException e) {
      log.error("Failed to execute keyboard markup in chat: {}", chatId, e);
    }
  }
}
