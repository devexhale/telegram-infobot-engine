package io.github.devexhale.botengine.execution.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.devexhale.botengine.execution.common.command.commandset.Command;
import io.github.devexhale.botengine.repository.message.MessageCleanupRepository;
import io.github.devexhale.botengine.testsupport.logger.TestLogCaptor;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessages;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@ExtendWith(MockitoExtension.class)
class MessageCleanupManagerTest {

  private static final String CHAT_ID = "123456789";
  private static final Integer MESSAGE_ID_1 = 100;
  private static final Integer MESSAGE_ID_2 = 101;
  private static final String COMMAND_TEXT = "/start";
  private static final String USER_TEXT = "hello";

  @Mock private TelegramClient client;
  @Mock private MessageCleanupRepository messageCleanupRepository;
  @Mock private Command startCommand;
  @Mock private Message mockMessage;

  @Captor private ArgumentCaptor<DeleteMessages> deleteMessagesCaptor;
  @Captor private ArgumentCaptor<DeleteMessage> deleteMessageCaptor;

  private MessageCleanupManager messageCleanupManager;
  private TestLogCaptor logCaptor;

  @BeforeEach
  void setUp() {
    logCaptor = new TestLogCaptor(MessageCleanupManager.class);
    messageCleanupManager =
        new MessageCleanupManager(client, messageCleanupRepository, List.of(startCommand));

    lenient().when(startCommand.getCommandName()).thenReturn(COMMAND_TEXT);
  }

  @AfterEach
  void tearDown() {
    logCaptor.close();
  }

  @Test
  void registerMessagesForCleanup_shouldSaveMessages_whenListIsNotEmpty() {
    when(mockMessage.getMessageId()).thenReturn(MESSAGE_ID_1);
    messageCleanupManager.registerMessagesForCleanup(CHAT_ID, List.of(mockMessage));
    verify(messageCleanupRepository).save(CHAT_ID, MESSAGE_ID_1);
  }

  @Test
  void registerMessagesForCleanup_shouldDoNothing_whenListIsNull() {
    messageCleanupManager.registerMessagesForCleanup(CHAT_ID, null);
    verify(messageCleanupRepository, never()).save(any(), any());
  }

  @Test
  void registerMessagesForCleanup_shouldDoNothing_whenListIsEmpty() {
    messageCleanupManager.registerMessagesForCleanup(CHAT_ID, List.of());
    verify(messageCleanupRepository, never()).save(any(), any());
  }

  @Test
  void cleanRedundantMessage_shouldDoNothing_whenMessageIsNull() throws TelegramApiException {
    messageCleanupManager.cleanRedundantMessage(null);
    verify(client, never()).executeAsync(any(DeleteMessage.class));
  }

  @Test
  void cleanRedundantMessage_shouldDeleteMessage_whenTextIsNull() throws TelegramApiException {
    when(mockMessage.getChatId()).thenReturn(Long.valueOf(CHAT_ID));
    when(mockMessage.getMessageId()).thenReturn(MESSAGE_ID_1);
    when(mockMessage.getText()).thenReturn(null);
    when(client.executeAsync(any(DeleteMessage.class)))
        .thenReturn(CompletableFuture.completedFuture(true));

    messageCleanupManager.cleanRedundantMessage(mockMessage);

    verify(client).executeAsync(any(DeleteMessage.class));
  }

  @Test
  void cleanRedundantMessage_shouldNotDeleteMessage_whenTextMatchesCommand()
      throws TelegramApiException {
    when(mockMessage.getText()).thenReturn(COMMAND_TEXT);
    when(mockMessage.getChatId()).thenReturn(Long.valueOf(CHAT_ID));
    when(mockMessage.getMessageId()).thenReturn(MESSAGE_ID_1);

    messageCleanupManager.cleanRedundantMessage(mockMessage);

    verify(client, never()).executeAsync(any(DeleteMessage.class));
  }

  @Test
  void cleanRedundantMessage_shouldDeleteMessage_whenTextDoesNotMatchCommand()
      throws TelegramApiException {
    when(mockMessage.getText()).thenReturn(USER_TEXT);
    when(mockMessage.getChatId()).thenReturn(Long.valueOf(CHAT_ID));
    when(mockMessage.getMessageId()).thenReturn(MESSAGE_ID_1);
    when(client.executeAsync(any(DeleteMessage.class)))
        .thenReturn(CompletableFuture.completedFuture(true));

    messageCleanupManager.cleanRedundantMessage(mockMessage);

    verify(client).executeAsync(deleteMessageCaptor.capture());
    assertEquals(CHAT_ID, deleteMessageCaptor.getValue().getChatId());
    assertEquals(MESSAGE_ID_1, deleteMessageCaptor.getValue().getMessageId());
  }

  @Test
  void cleanRedundantMessage_shouldLogWarning_whenAsyncDeleteFails() throws TelegramApiException {
    when(mockMessage.getText()).thenReturn(USER_TEXT);
    when(mockMessage.getChatId()).thenReturn(Long.valueOf(CHAT_ID));
    when(mockMessage.getMessageId()).thenReturn(MESSAGE_ID_1);
    when(client.executeAsync(any(DeleteMessage.class)))
        .thenReturn(CompletableFuture.failedFuture(new TelegramApiException("async fail")));

    messageCleanupManager.cleanRedundantMessage(mockMessage);

    ILoggingEvent loggedEvent = logCaptor.events().getFirst();
    assertEquals(Level.WARN, loggedEvent.getLevel());
    assertTrue(loggedEvent.getFormattedMessage().contains("Failed to delete message from chat"));
    assertTrue(loggedEvent.getFormattedMessage().contains(CHAT_ID));
    assertTrue(loggedEvent.getFormattedMessage().contains(String.valueOf(MESSAGE_ID_1)));
  }

  @Test
  void cleanRedundantMessage_shouldLogWarning_whenExecuteAsyncThrowsTelegramApiException()
      throws TelegramApiException {
    when(mockMessage.getText()).thenReturn(USER_TEXT);
    when(mockMessage.getChatId()).thenReturn(Long.valueOf(CHAT_ID));
    when(mockMessage.getMessageId()).thenReturn(MESSAGE_ID_1);
    when(client.executeAsync(any(DeleteMessage.class)))
        .thenThrow(new TelegramApiException("sync fail"));

    messageCleanupManager.cleanRedundantMessage(mockMessage);

    ILoggingEvent loggedEvent = logCaptor.events().getFirst();
    assertEquals(Level.WARN, loggedEvent.getLevel());
    assertTrue(loggedEvent.getFormattedMessage().contains("Failed to initiate message deletion"));
    assertTrue(loggedEvent.getFormattedMessage().contains(CHAT_ID));
    assertTrue(loggedEvent.getFormattedMessage().contains(String.valueOf(MESSAGE_ID_1)));
  }

  @Test
  void cleanLastNode_shouldDoNothing_whenRepoReturnsEmptySet() throws TelegramApiException {
    when(messageCleanupRepository.deleteAllByChatId(CHAT_ID)).thenReturn(Set.of());
    messageCleanupManager.cleanLastNode(CHAT_ID);
    verify(client, never()).executeAsync(any(DeleteMessages.class));
  }

  @Test
  void cleanLastNode_shouldDeleteMessages_whenRepoReturnsIds() throws TelegramApiException {
    when(messageCleanupRepository.deleteAllByChatId(CHAT_ID))
        .thenReturn(Set.of(MESSAGE_ID_1, MESSAGE_ID_2));
    when(client.executeAsync(any(DeleteMessages.class)))
        .thenReturn(CompletableFuture.completedFuture(true));

    messageCleanupManager.cleanLastNode(CHAT_ID);

    verify(client).executeAsync(deleteMessagesCaptor.capture());
    assertEquals(CHAT_ID, deleteMessagesCaptor.getValue().getChatId());
    assertEquals(
        Set.of(MESSAGE_ID_1, MESSAGE_ID_2),
        Set.copyOf(deleteMessagesCaptor.getValue().getMessageIds()));
  }

  @Test
  void cleanLastNode_shouldDoNothing_whenRepoReturnsNull() throws TelegramApiException {
    when(messageCleanupRepository.deleteAllByChatId(CHAT_ID)).thenReturn(null);
    messageCleanupManager.cleanLastNode(CHAT_ID);
    verify(client, never()).executeAsync(any(DeleteMessages.class));
  }

  @Test
  void cleanLastNode_shouldLogWarning_whenAsyncDeleteFails() throws TelegramApiException {
    when(messageCleanupRepository.deleteAllByChatId(CHAT_ID)).thenReturn(Set.of(MESSAGE_ID_1));
    when(client.executeAsync(any(DeleteMessages.class)))
        .thenReturn(CompletableFuture.failedFuture(new TelegramApiException("fail")));

    messageCleanupManager.cleanLastNode(CHAT_ID);

    ILoggingEvent loggedEvent = logCaptor.events().getFirst();
    assertEquals(Level.WARN, loggedEvent.getLevel());
    assertTrue(
        loggedEvent.getFormattedMessage().contains("Failed to delete message batch from chat"));
    assertTrue(loggedEvent.getFormattedMessage().contains(CHAT_ID));
    assertTrue(loggedEvent.getFormattedMessage().contains(String.valueOf(MESSAGE_ID_1)));
  }

  @Test
  void cleanLastNode_shouldLogWarning_whenExecuteAsyncThrowsTelegramApiException()
      throws TelegramApiException {
    when(messageCleanupRepository.deleteAllByChatId(CHAT_ID)).thenReturn(Set.of(MESSAGE_ID_1));
    when(client.executeAsync(any(DeleteMessages.class)))
        .thenThrow(new TelegramApiException("sync batch fail"));

    messageCleanupManager.cleanLastNode(CHAT_ID);

    ILoggingEvent loggedEvent = logCaptor.events().getFirst();
    assertEquals(Level.WARN, loggedEvent.getLevel());
    assertTrue(
        loggedEvent.getFormattedMessage().contains("Failed to initiate message batch deletion"));
    assertTrue(loggedEvent.getFormattedMessage().contains(CHAT_ID));
    assertTrue(loggedEvent.getFormattedMessage().contains(String.valueOf(MESSAGE_ID_1)));
  }
}
