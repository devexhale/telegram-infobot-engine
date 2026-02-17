package com.github.jawisimo.tbcfstarter.interaction.keyboard;

import com.github.jawisimo.tbcfstarter.interaction.node.model.Button;
import com.github.jawisimo.tbcfstarter.interaction.node.model.ButtonType;
import com.github.jawisimo.tbcfstarter.interaction.node.model.DialogNode;
import com.github.jawisimo.tbcfstarter.repository.MessageRepository;
import com.github.jawisimo.tbcfstarter.validator.DialogValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeyboardExecutor {
  private final TelegramClient client;
  private final KeyboardMarkupBuilder keyboardBuilder;
  private final DialogValidator dialogValidator;
  private final MessageRepository messageRepository;

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
      log.error("Failed to handle keyboard markup in chat: {}", chatId, e);
    }
  }
}
