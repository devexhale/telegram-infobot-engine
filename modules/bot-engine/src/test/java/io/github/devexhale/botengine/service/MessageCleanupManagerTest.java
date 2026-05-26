// package io.github.jawisimo.botengine.service;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;
//
// import support.execution.io.github.devexhale.botengine.MessageCleanupManager;
// import io.github.jawisimo.botengine.execution.common.commandset.command.Command;
// import io.github.jawisimo.botengine.repository.message.dialog.MessageCleanupRepository;
// import java.util.List;
// import java.util.stream.Stream;
//
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.params.ParameterizedTest;
// import org.junit.jupiter.params.provider.Arguments;
// import org.junit.jupiter.params.provider.MethodSource;
// import org.mockito.ArgumentCaptor;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
// import org.telegram.telegrambots.meta.api.objects.message.Message;
// import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
// import org.telegram.telegrambots.meta.generics.TelegramClient;
//
// @ExtendWith(MockitoExtension.class)
// class MessageCleanupManagerTest {
//
//  private static final Long CHAT_ID = 125L;
//  private static final Integer MESSAGE_ID = 59;
//
//  private static final Long SECOND_CHAT_ID = 124L;
//  private static final Long COMMAND_CHAT_ID = 150L;
//  private static final Long PARAM_CHAT_ID = 120L;
//
//  private static final Integer SECOND_MESSAGE_ID = 3;
//  private static final Integer COMMAND_MESSAGE_ID = 2;
//  private static final Integer PARAM_MESSAGE_ID = 8;
//
//  private static final String START_COMMAND = "/start";
//  private static final String LAST_COMMAND = "/last";
//  private static final String NON_COMMAND_TEXT = "hello_world";
//
//  private static final String TELEGRAM_API_EXCEPTION_MSG = "Telegram API exception";
//
//  private final TelegramClient client = mock(TelegramClient.class);
//  private final MessageCleanupRepository messageRepository = mock(MessageCleanupRepository.class);
//
//  private final Command startCommand = mock(Command.class);
//  private final Command lastCommand = mock(Command.class);
//
//  private MessageCleanupManager service;
//
//  private final Message message = mock(Message.class);
//
//  private void initServiceWithCommands(List<Command> commands) {
//    service = new MessageCleanupManager(client, messageRepository, commands);
//  }
//
//  @Test
//  void deleteMessage_shouldCallTelegramClient_WithCorrectParameters() throws Exception {
//    initServiceWithCommands(List.of());
//
//    service.deleteMessage(CHAT_ID.toString(), MESSAGE_ID);
//
//    ArgumentCaptor<DeleteMessage> captor = ArgumentCaptor.forClass(DeleteMessage.class);
//    verify(client).executeAsync(captor.capture());
//
//    DeleteMessage request = captor.getValue();
//    assertEquals(CHAT_ID.toString(), request.getChatId());
//    assertEquals(MESSAGE_ID, request.getMessageId());
//  }
//
//  @Test
//  void deleteMessage_shouldNotThrow_whenTelegramClientFails() throws Exception {
//    initServiceWithCommands(List.of());
//
//    doThrow(new TelegramApiException(TELEGRAM_API_EXCEPTION_MSG))
//        .when(client)
//        .executeAsync(any(DeleteMessage.class));
//
//    assertDoesNotThrow(() -> service.deleteMessage(CHAT_ID.toString(), MESSAGE_ID));
//
//    verify(client).executeAsync(any(DeleteMessage.class));
//  }
//
//  @Test
//  void deleteRedundantMessage_shouldDoNothing_whenMessageIsNull() {
//    initServiceWithCommands(List.of());
//
//    service.deleteRedundantMessage(null);
//
//    verifyNoInteractions(client, messageRepository);
//  }
//
//  @Test
//  void deleteRedundantMessage_shouldDelete_whenMessageTextIsNull() throws TelegramApiException {
//    initServiceWithCommands(List.of());
//
//    when(message.getText()).thenReturn(null);
//    when(message.getChatId()).thenReturn(CHAT_ID);
//    when(message.getMessageId()).thenReturn(MESSAGE_ID);
//
//    service.deleteRedundantMessage(message);
//
//    verify(client).executeAsync(any(DeleteMessage.class));
//  }
//
//  @Test
//  void deleteRedundantMessage_shouldNotDelete_whenMessageIsCommand() {
//    when(startCommand.getCommandName()).thenReturn(START_COMMAND);
//
//    initServiceWithCommands(List.of(startCommand));
//
//    when(message.getText()).thenReturn(START_COMMAND);
//    when(message.getChatId()).thenReturn(COMMAND_CHAT_ID);
//    when(message.getMessageId()).thenReturn(COMMAND_MESSAGE_ID);
//
//    service.deleteRedundantMessage(message);
//
//    verifyNoInteractions(client);
//  }
//
//  @Test
//  void deleteRedundantMessage_shouldDelete_whenMessageIsNotCommand() throws TelegramApiException {
//    when(startCommand.getCommandName()).thenReturn(START_COMMAND);
//
//    initServiceWithCommands(List.of(startCommand));
//
//    when(message.getText()).thenReturn(NON_COMMAND_TEXT);
//    when(message.getChatId()).thenReturn(SECOND_CHAT_ID);
//    when(message.getMessageId()).thenReturn(SECOND_MESSAGE_ID);
//
//    service.deleteRedundantMessage(message);
//
//    verify(client).executeAsync(any(DeleteMessage.class));
//  }
//
//  @ParameterizedTest
//  @MethodSource("provideCommandMessages")
//  void deleteRedundantMessage_shouldNotDelete_whenTextMatchesAnyCommand(String commandText) {
//    when(startCommand.getCommandName()).thenReturn(START_COMMAND);
//    when(lastCommand.getCommandName()).thenReturn(LAST_COMMAND);
//
//    initServiceWithCommands(List.of(startCommand, lastCommand));
//
//    when(message.getText()).thenReturn(commandText);
//    when(message.getChatId()).thenReturn(PARAM_CHAT_ID);
//    when(message.getMessageId()).thenReturn(PARAM_MESSAGE_ID);
//
//    service.deleteRedundantMessage(message);
//
//    verifyNoInteractions(client);
//  }
//
//  @ParameterizedTest
//  @MethodSource("provideNonCommandMessages")
//  void deleteRedundantMessage_shouldDelete_whenTextDoesNotMatchAnyCommand(
//      String text, Long chatId, Integer messageId) throws TelegramApiException {
//
//    when(startCommand.getCommandName()).thenReturn(START_COMMAND);
//    when(lastCommand.getCommandName()).thenReturn(LAST_COMMAND);
//
//    initServiceWithCommands(List.of(startCommand, lastCommand));
//
//    when(message.getText()).thenReturn(text);
//    when(message.getChatId()).thenReturn(chatId);
//    when(message.getMessageId()).thenReturn(messageId);
//
//    service.deleteRedundantMessage(message);
//
//    ArgumentCaptor<DeleteMessage> captor = ArgumentCaptor.forClass(DeleteMessage.class);
//    verify(client).executeAsync(captor.capture());
//
//    DeleteMessage request = captor.getValue();
//    assertEquals(chatId.toString(), request.getChatId());
//    assertEquals(messageId, request.getMessageId());
//  }
//
//  @Test
//  void clearLastNode_shouldDoNothing_whenRepositoryReturnsNull() {
//    initServiceWithCommands(List.of());
//
//    when(messageRepository.removeAll(CHAT_ID.toString())).thenReturn(null);
//
//    service.clearLastNode(CHAT_ID.toString());
//
//    verify(messageRepository).removeAll(CHAT_ID.toString());
//    verifyNoInteractions(client);
//  }
//
//  @Test
//  void clearLastNode_shouldDoNothing_whenRepositoryReturnsEmptyList() {
//    initServiceWithCommands(List.of());
//
//    when(messageRepository.removeAll(CHAT_ID.toString())).thenReturn(List.of());
//
//    service.clearLastNode(CHAT_ID.toString());
//
//    verify(messageRepository).removeAll(CHAT_ID.toString());
//    verifyNoInteractions(client);
//  }
//
//  @Test
//  void clearLastNode_shouldDeleteAllMessagesFromRepository() throws Exception {
//    initServiceWithCommands(List.of());
//
//    List<Integer> messageIds = List.of(1, 2, 3);
//    when(messageRepository.removeAll(CHAT_ID.toString())).thenReturn(messageIds);
//
//    service.clearLastNode(CHAT_ID.toString());
//
//    ArgumentCaptor<DeleteMessage> captor = ArgumentCaptor.forClass(DeleteMessage.class);
//    verify(client, times(messageIds.size())).executeAsync(captor.capture());
//
//    List<DeleteMessage> requests = captor.getAllValues();
//
//    assertEquals(messageIds.size(), requests.size());
//    requests.forEach(request -> assertEquals(CHAT_ID.toString(), request.getChatId()));
//
//    List<Integer> capturedIds = requests.stream().map(DeleteMessage::getMessageId).toList();
//
//    assertTrue(capturedIds.containsAll(messageIds));
//  }
//
//  @Test
//  void clearLastNode_shouldContinueDeletingEvenIfSomeDeletionsFail() throws Exception {
//    initServiceWithCommands(List.of());
//
//    List<Integer> messageIds = List.of(1, 2, 3);
//    when(messageRepository.removeAll(CHAT_ID.toString())).thenReturn(messageIds);
//
//    doThrow(new TelegramApiException(TELEGRAM_API_EXCEPTION_MSG))
//        .when(client)
//        .executeAsync(any(DeleteMessage.class));
//
//    assertDoesNotThrow(() -> service.clearLastNode(CHAT_ID.toString()));
//
//    verify(client, times(messageIds.size())).executeAsync(any(DeleteMessage.class));
//  }
//
//  @Test
//  void clearLastNode_shouldUseChatIdStringCorrectly() throws Exception {
//    initServiceWithCommands(List.of());
//
//    when(messageRepository.removeAll(CHAT_ID.toString())).thenReturn(List.of(42));
//
//    service.clearLastNode(CHAT_ID.toString());
//
//    ArgumentCaptor<DeleteMessage> captor = ArgumentCaptor.forClass(DeleteMessage.class);
//    verify(client).executeAsync(captor.capture());
//
//    assertEquals(CHAT_ID.toString(), captor.getValue().getChatId());
//  }
//
//  static Stream<Arguments> provideCommandMessages() {
//    return Stream.of(Arguments.of(START_COMMAND), Arguments.of(LAST_COMMAND));
//  }
//
//  static Stream<Arguments> provideNonCommandMessages() {
//    return Stream.of(
//        Arguments.of("", 423L, 2),
//        Arguments.of("hello", 54L, 3),
//        Arguments.of("/start ", 5333L, 4),
//        Arguments.of(" /start", 6335L, 5),
//        Arguments.of("START", 23L, 6),
//        Arguments.of("/start something", 464L, 7),
//        Arguments.of("/last ", 464L, 8),
//        Arguments.of(" /last", 464L, 9),
//        Arguments.of("LAST", 464L, 10),
//        Arguments.of("/last something", 464L, 11),
//        Arguments.of("/unknown", 45L, 12));
//  }
// }
