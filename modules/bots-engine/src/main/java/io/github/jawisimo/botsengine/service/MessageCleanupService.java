package io.github.jawisimo.botsengine.service;

import io.github.jawisimo.botsengine.interaction.command.commandset.Command;
import io.github.jawisimo.botsengine.repository.MessageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Deletes bot messages and redundant user messages to keep the chat clean during dialog navigation.
 *
 * <p>Uses {@link MessageRepository} as a source of message identifiers to delete and delegates the
 * actual deletion to {@link TelegramClient}.
 *
 * <p>The service is typically used when moving between dialog nodes to remove messages from the
 * previous node and to clean up redundant user input.
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
   * <p>Messages containing bot commands are preserved to keep visible entry points in the chat
   * history. All other messages, including text and media (photos, videos, documents, etc.), are
   * removed to keep the chat clean and prevent clutter during dialog navigation.
   *
   * <p>A message is considered a command only if its text matches one of the registered command
   * names. Messages without text (e.g., media messages) are always treated as redundant and
   * deleted.
   *
   * @param message the message to evaluate and possibly delete
   */
  public void deleteRedundantMessage(Message message) {
    if (message == null) {
      return;
    }

    if (message.getChatId() == null || message.getMessageId() == null) {
      return;
    }

    String chatId = message.getChatId().toString();
    Integer messageId = message.getMessageId();

    String text = message.getText();

    if (text != null) {
      for (Command command : commands) {
        if (text.equals(command.getCommandName())) {
          return;
        }
      }
    }

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
