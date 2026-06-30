package io.github.devexhale.botengine.execution.support;

import io.github.devexhale.botengine.execution.common.command.commandset.Command;
import io.github.devexhale.botengine.repository.message.MessageCleanupRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessages;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Manages the deletion of bot and redundant user messages to keep the chat clean.
 *
 * <p>Uses {@link MessageCleanupRepository} to track messages and delegates actual deletion to
 * {@link TelegramClient}.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MessageCleanupManager {

  private final TelegramClient client;
  private final MessageCleanupRepository messageCleanupRepository;
  private final List<Command> commands;

  private static final int BATCH_SIZE = 100;

  /**
   * Registers sent messages for future cleanup.
   *
   * @param chatId the chat identifier
   * @param messages the list of messages to register
   */
  public void registerMessagesForCleanup(String chatId, List<Message> messages) {
    if (messages != null && !messages.isEmpty()) {
      messages.forEach(message -> messageCleanupRepository.save(chatId, message.getMessageId()));
    }
  }

  /**
   * Deletes a user message if it is not a recognized bot command.
   *
   * @param message the message to evaluate and possibly delete
   */
  public void cleanRedundantMessage(Message message) {
    if (message == null) {
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
   * Deletes all previously registered messages for the given chat in batches.
   *
   * @param chatId the chat identifier
   */
  public void cleanLastNode(String chatId) {
    Set<Integer> messageIds = messageCleanupRepository.deleteAllByChatId(chatId);

    if (messageIds == null || messageIds.isEmpty()) {
      return;
    }

    List<Integer> ids = new ArrayList<>(messageIds);

    for (int i = 0; i < ids.size(); i += BATCH_SIZE) {
      List<Integer> batch = ids.subList(i, Math.min(i + BATCH_SIZE, ids.size()));
      deleteMessages(chatId, batch);
    }
  }

  private void deleteMessages(String chatId, List<Integer> messageIds) {
    String formattedIds = formatMessageIds(messageIds);

    try {
      DeleteMessages deleteMessages =
          DeleteMessages.builder().chatId(chatId).messageIds(messageIds).build();

      client
          .executeAsync(deleteMessages)
          .exceptionally(
              e -> {
                log.warn(
                    "Failed to delete message batch from chat. ChatID={} ; MessageIDs={}",
                    chatId,
                    formattedIds,
                    e);
                return null;
              });
    } catch (TelegramApiException e) {
      log.warn(
          "Failed to initiate message batch deletion. ChatID={} ; MessageIDs={}",
          chatId,
          formattedIds,
          e);
    }
  }

  private void deleteMessage(String chatId, Integer messageId) {
    try {
      client
          .executeAsync(DeleteMessage.builder().chatId(chatId).messageId(messageId).build())
          .exceptionally(
              e -> {
                log.warn(
                    "Failed to delete message from chat. ChatID={} ; MessageID={}",
                    chatId,
                    messageId,
                    e);
                return null;
              });
    } catch (TelegramApiException e) {
      log.warn(
          "Failed to initiate message deletion. ChatID={} ; MessageID={}", chatId, messageId, e);
    }
  }

  /**
   * Formats a list of message identifiers into a string for logging.
   *
   * @param messageIds the list of identifiers
   * @return a formatted string like "[1, 2, 3]"
   */
  private String formatMessageIds(List<Integer> messageIds) {
    return messageIds.stream().map(String::valueOf).collect(Collectors.joining(", ", "[", "]"));
  }
}
