package com.github.jawisimo.botengine.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.github.jawisimo.botengine.interaction.command.commandset.StartCommand;
import com.github.jawisimo.botengine.repository.MessageRepository;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@ExtendWith(MockitoExtension.class)
class MessageCleanupServiceTest {
  private static final String CHAT_ID = "chat-12";
  private static final Integer MESSAGE_ID = 59;

  @Mock private TelegramClient client;
  @Mock private MessageRepository messageRepository;

  @InjectMocks private MessageCleanupService service;

  @Test
  void deleteMessage_shouldCallTelegramClient_WithCorrectParameters() throws Exception {
    service.deleteMessage(CHAT_ID, MESSAGE_ID);

    ArgumentCaptor<DeleteMessage> captor = ArgumentCaptor.forClass(DeleteMessage.class);
    verify(client).executeAsync(captor.capture());

    DeleteMessage request = captor.getValue();
    assertEquals(CHAT_ID, request.getChatId());
    assertEquals(MESSAGE_ID, request.getMessageId());
  }

  @Test
  void deleteMessage_shouldLogWarningAndNotThrow_whenTelegramClientFails() throws Exception {
    doThrow(new TelegramApiException("API error"))
        .when(client)
        .executeAsync(any(DeleteMessage.class));

    assertDoesNotThrow(() -> service.deleteMessage(CHAT_ID, MESSAGE_ID));

    verify(client).executeAsync(any(DeleteMessage.class));
  }

  @Test
  void deleteRedundantMessage_shouldDoNothing_whenMessageIsNull() {
    service.deleteRedundantMessage(null);

    verifyNoInteractions(client);
    verifyNoInteractions(messageRepository);
  }

  @Test
  void deleteRedundantMessage_shouldDoNothing_whenMessageTextIsExactlyStartCommand() {
    Message message = mock(Message.class);
    when(message.getText()).thenReturn(StartCommand.COMMAND_NAME);

    service.deleteRedundantMessage(message);

    verify(message).getText();
    verify(client, never()).executeAsync((SendDocument) any());
    verify(messageRepository, never()).removeAll(any());
  }

  @ParameterizedTest
  @MethodSource("provideNonStartMessages")
  void deleteRedundantMessage_shouldDeleteMessage_whenMessageIsNotStartCommand(
      String text, Long chatId, Integer messageId) throws Exception {
    Message message = mock(Message.class);

    when(message.getText()).thenReturn(text);
    when(message.getChatId()).thenReturn(chatId);
    when(message.getMessageId()).thenReturn(messageId);

    service.deleteRedundantMessage(message);

    ArgumentCaptor<DeleteMessage> captor = ArgumentCaptor.forClass(DeleteMessage.class);
    verify(client).executeAsync(captor.capture());

    DeleteMessage request = captor.getValue();
    assertEquals(chatId.toString(), request.getChatId());
    assertEquals(messageId, request.getMessageId());
  }

  @Test
  void clearLastNode_shouldDoNothing_whenRepositoryReturnsNull() {
    when(messageRepository.removeAll(CHAT_ID)).thenReturn(null);

    service.clearLastNode(CHAT_ID);

    verify(messageRepository).removeAll(CHAT_ID);
    verifyNoInteractions(client);
  }

  @Test
  void clearLastNode_shouldDoNothing_whenRepositoryReturnsEmptyList() {
    when(messageRepository.removeAll(CHAT_ID)).thenReturn(List.of());

    service.clearLastNode(CHAT_ID);

    verify(messageRepository).removeAll(CHAT_ID);
    verifyNoInteractions(client);
  }

  @Test
  void clearLastNode_shouldDeleteAllMessagesFromRepository() throws Exception {
    List<Integer> messageIds = List.of(1, 2, 3);

    when(messageRepository.removeAll(CHAT_ID)).thenReturn(messageIds);

    service.clearLastNode(CHAT_ID);

    ArgumentCaptor<DeleteMessage> captor = ArgumentCaptor.forClass(DeleteMessage.class);
    verify(client, times(messageIds.size())).executeAsync(captor.capture());

    List<DeleteMessage> requests = captor.getAllValues();
    List<Integer> capturedIds = requests.stream().map(DeleteMessage::getMessageId).toList();

    assertEquals(messageIds.size(), requests.size());
    requests.forEach(request -> assertEquals(CHAT_ID, request.getChatId()));
    assertTrue(capturedIds.containsAll(messageIds));
  }

  @Test
  void clearLastNode_shouldContinueDeletingEvenIfSomeDeletionsFail() throws Exception {
    List<Integer> messageIds = List.of(1, 2, 3);

    when(messageRepository.removeAll(CHAT_ID)).thenReturn(messageIds);
    doThrow(new TelegramApiException("API error"))
        .when(client)
        .executeAsync(any(DeleteMessage.class));

    assertDoesNotThrow(() -> service.clearLastNode(CHAT_ID));

    verify(client, times(messageIds.size())).executeAsync(any(DeleteMessage.class));
  }

  @Test
  void clearLastNode_shouldUseChatIdStringCorrectly() throws Exception {
    List<Integer> messageIds = List.of(42);

    when(messageRepository.removeAll(CHAT_ID)).thenReturn(messageIds);

    service.clearLastNode(CHAT_ID);

    ArgumentCaptor<DeleteMessage> captor = ArgumentCaptor.forClass(DeleteMessage.class);
    verify(client).executeAsync(captor.capture());

    assertEquals(CHAT_ID, captor.getValue().getChatId());
  }

  static Stream<Arguments> provideNonStartMessages() {
    return Stream.of(
        Arguments.of(null, 123L, 1),
        Arguments.of("", 123L, 2),
        Arguments.of("hello", 123L, 3),
        Arguments.of("/start ", 123L, 4),
        Arguments.of(" /start", 123L, 5),
        Arguments.of("START", 123L, 6),
        Arguments.of("/start something", 123L, 7));
  }
}
