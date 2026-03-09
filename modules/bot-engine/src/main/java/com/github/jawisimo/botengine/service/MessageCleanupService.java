package com.github.jawisimo.botengine.service;

import com.github.jawisimo.botengine.interaction.command.commandset.Command;
import com.github.jawisimo.botengine.interaction.command.commandset.StartCommand;
import com.github.jawisimo.botengine.repository.MessageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Deletes previously sent bot messages to keep the chat clean during dialog navigation.
 *
 * <p>Uses {@link MessageRepository} as a source of message identifiers to delete and delegates
 * deletion to {@link TelegramClient}. The service is typically used when moving between dialog
 * nodes to remove messages from the previous node.
 *
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageCleanupService {

  private final TelegramClient client;
  private final MessageRepository messageRepository;
  private final List<Command> commands;

  /**
   * Deletes a message by chat and message identifiers.
   *
   * <p>Note: This method is public for potential future use by other services. Currently used only
   * within this class.
   *
   * @param chatId the chat identifier
   * @param messageId the message identifier
   */
  public void deleteMessage(String chatId, Integer messageId) {
    try {
      client.executeAsync(DeleteMessage.builder().chatId(chatId).messageId(messageId).build());
    } catch (TelegramApiException e) {
      log.warn("Failed to delete message: {} from chat: {}", messageId, chatId, e);
    }
  }

  /**
   * Deletes a user message if it is considered redundant for the dialog flow.
   *
   * <p>Messages containing the {@link StartCommand#COMMAND_NAME} command are kept to avoid removing
   * the conversation entry point from the chat history.
   *
   * @param message the message to evaluate and possibly delete
   */
  public void deleteRedundantMessage(Message message) {
    if (message == null) {
      return;
    }

    String text = message.getText();

    if (text == null) return;

    for (Command command : commands) {
      if (text.equals(command.getCommandName())) {
        command.getCommand();
        return;
      }
    }

    String chatId = message.getChatId().toString();
    Integer messageId = message.getMessageId();
    deleteMessage(chatId, messageId);
  }

  /**
   * Deletes all messages associated with the last dialog node for the given chat.
   *
   * @param chatId the chat identifier
   */
  public void clearLastNode(String chatId) {
    List<Integer> messageIds = messageRepository.removeAll(chatId);

    if (messageIds == null || messageIds.isEmpty()) {
      return;
    }

    for (Integer messageId : messageIds) {
      deleteMessage(chatId, messageId);
    }
  }
}
