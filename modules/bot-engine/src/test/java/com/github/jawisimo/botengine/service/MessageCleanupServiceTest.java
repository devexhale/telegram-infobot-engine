package com.github.jawisimo.botengine.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.github.jawisimo.botengine.interaction.command.commandset.Command;
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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@ExtendWith(MockitoExtension.class)
class MessageCleanupServiceTest {

  private static final String CHAT_ID = "chat-12";
  private static final Integer MESSAGE_ID = 59;
  private static final String START_COMMAND = "/start";
  private static final String LAST_COMMAND = "/last";

  @Mock private TelegramClient client;
  @Mock private MessageRepository messageRepository;
  @Mock private List<Command> commands;
  @Mock private Message message;
  @Mock private Command startCommand;
  @Mock private Command lastCommand;

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
  void deleteRedundantMessage_shouldDoNothing_whenMessageTextIsNull() {
    when(message.getText()).thenReturn(null);

    service.deleteRedundantMessage(message);

    verify(message).getText();
    verifyNoInteractions(client);
  }

  @ParameterizedTest
  @MethodSource("provideCommandMessages")
  void deleteRedundantMessage_shouldNotDeleteMessage_whenTextMatchesAnyCommand(String commandText) {
    lenient().when(commands.iterator()).thenReturn(List.of(startCommand, lastCommand).iterator());
    lenient().when(startCommand.getCommandName()).thenReturn(START_COMMAND);
    lenient().when(lastCommand.getCommandName()).thenReturn(LAST_COMMAND);
    when(message.getText()).thenReturn(commandText);

    service.deleteRedundantMessage(message);

    verify(message).getText();
    verify(startCommand, atMostOnce()).getCommand();
    verify(lastCommand, atMostOnce()).getCommand();
    verifyNoInteractions(client);
  }

  @ParameterizedTest
  @MethodSource("provideNonCommandMessages")
  void deleteRedundantMessage_shouldDeleteMessage_whenTextDoesNotMatchAnyCommand(
      String text, Long chatId, Integer messageId) throws Exception {
    when(commands.iterator()).thenReturn(List.of(startCommand, lastCommand).iterator());
    when(startCommand.getCommandName()).thenReturn(START_COMMAND);
    when(lastCommand.getCommandName()).thenReturn(LAST_COMMAND);
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

  static Stream<Arguments> provideCommandMessages() {
    return Stream.of(Arguments.of(START_COMMAND), Arguments.of(LAST_COMMAND));
  }

  static Stream<Arguments> provideNonCommandMessages() {
    return Stream.of(
        Arguments.of("", 423L, 2),
        Arguments.of("hello", 54L, 3),
        Arguments.of("/start ", 5333L, 4),
        Arguments.of(" /start", 6335L, 5),
        Arguments.of("START", 23L, 6),
        Arguments.of("/start something", 464L, 7),
        Arguments.of("/last ", 464L, 8),
        Arguments.of(" /last", 464L, 9),
        Arguments.of("LAST", 464L, 10),
        Arguments.of("/last something", 464L, 11),
        Arguments.of("/unknown", 45L, 12));
  }
}
