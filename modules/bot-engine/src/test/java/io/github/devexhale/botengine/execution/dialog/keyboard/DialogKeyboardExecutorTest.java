package io.github.devexhale.botengine.execution.dialog.keyboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.devexhale.botengine.diagnostics.exception.TelegramMessageSendException;
import io.github.devexhale.botengine.domain.dialog.Button;
import io.github.devexhale.botengine.domain.dialog.ButtonType;
import io.github.devexhale.botengine.domain.dialog.DialogNode;
import io.github.devexhale.botengine.testsupport.logger.TestLogCaptor;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@ExtendWith(MockitoExtension.class)
class DialogKeyboardExecutorTest {

  private static final String CHAT_ID = "123456789";
  private static final String NODE_MESSAGE = "Hello from node!";
  private static final String BUTTON_LABEL = "Click me";
  private static final String BUTTON_CALLBACK_DATA = "callback_data";
  private static final String BUTTON_URL = "https://example.com";

  @Mock private TelegramClient client;
  @Mock private DialogKeyboardMarkupBuilder keyboardBuilder;

  @InjectMocks private DialogKeyboardExecutor dialogKeyboardExecutor;

  @Mock private Message mockSentMessage;

  @Captor private ArgumentCaptor<SendMessage> sendMessageCaptor;

  private Button button;

  @BeforeEach
  void setUp() {
    button = new Button(BUTTON_LABEL, BUTTON_CALLBACK_DATA, BUTTON_URL);
  }

  @Test
  void execute_shouldSendInlineKeyboard_whenNodeTypeIsNull() throws TelegramApiException {
    List<Button> buttons = List.of(button);
    DialogNode node = new DialogNode(null, NODE_MESSAGE, null, buttons);

    InlineKeyboardMarkup expectedInlineMarkup = InlineKeyboardMarkup.builder().build();

    when(keyboardBuilder.buildInlineKeyboard(buttons)).thenReturn(expectedInlineMarkup);
    when(client.execute(any(SendMessage.class))).thenReturn(mockSentMessage);

    List<Message> result = dialogKeyboardExecutor.execute(node, CHAT_ID);

    verify(keyboardBuilder).buildInlineKeyboard(buttons);
    verify(keyboardBuilder, never()).buildReplyKeyboard(any());
    verify(client).execute(sendMessageCaptor.capture());

    SendMessage capturedRequest = sendMessageCaptor.getValue();
    assertEquals(CHAT_ID, capturedRequest.getChatId());
    assertEquals(NODE_MESSAGE, capturedRequest.getText());
    assertEquals(expectedInlineMarkup, capturedRequest.getReplyMarkup());

    assertIterableEquals(List.of(mockSentMessage), result);
  }

  @Test
  void execute_shouldSendInlineKeyboard_whenNodeTypeIsInline() throws TelegramApiException {
    List<Button> buttons = List.of(button);
    DialogNode node = new DialogNode(null, NODE_MESSAGE, ButtonType.INLINE, buttons);

    InlineKeyboardMarkup expectedInlineMarkup = InlineKeyboardMarkup.builder().build();

    when(keyboardBuilder.buildInlineKeyboard(buttons)).thenReturn(expectedInlineMarkup);
    when(client.execute(any(SendMessage.class))).thenReturn(mockSentMessage);

    List<Message> result = dialogKeyboardExecutor.execute(node, CHAT_ID);

    verify(keyboardBuilder).buildInlineKeyboard(buttons);
    verify(keyboardBuilder, never()).buildReplyKeyboard(any());
    verify(client).execute(sendMessageCaptor.capture());

    SendMessage capturedRequest = sendMessageCaptor.getValue();
    assertEquals(CHAT_ID, capturedRequest.getChatId());
    assertEquals(NODE_MESSAGE, capturedRequest.getText());
    assertEquals(expectedInlineMarkup, capturedRequest.getReplyMarkup());

    assertIterableEquals(List.of(mockSentMessage), result);
  }

  @Test
  void execute_shouldSendReplyKeyboard_whenNodeTypeIsReply() throws TelegramApiException {
    List<Button> buttons = List.of(button);
    DialogNode node = new DialogNode(null, NODE_MESSAGE, ButtonType.REPLY, buttons);

    ReplyKeyboardMarkup expectedReplyMarkup = ReplyKeyboardMarkup.builder().build();

    when(keyboardBuilder.buildReplyKeyboard(buttons)).thenReturn(expectedReplyMarkup);
    when(client.execute(any(SendMessage.class))).thenReturn(mockSentMessage);

    List<Message> result = dialogKeyboardExecutor.execute(node, CHAT_ID);

    verify(keyboardBuilder).buildReplyKeyboard(buttons);
    verify(keyboardBuilder, never()).buildInlineKeyboard(any());
    verify(client).execute(sendMessageCaptor.capture());

    SendMessage capturedRequest = sendMessageCaptor.getValue();
    assertEquals(CHAT_ID, capturedRequest.getChatId());
    assertEquals(NODE_MESSAGE, capturedRequest.getText());
    assertEquals(expectedReplyMarkup, capturedRequest.getReplyMarkup());

    assertIterableEquals(List.of(mockSentMessage), result);
  }

  @Test
  void execute_shouldThrowIllegalStateException_whenButtonTypeIsUnsupported()
      throws TelegramApiException {
    List<Button> buttons = List.of(button);
    DialogNode node = new DialogNode(null, NODE_MESSAGE, ButtonType.UNKNOWN, buttons);

    IllegalStateException thrown =
        assertThrows(
            IllegalStateException.class, () -> dialogKeyboardExecutor.execute(node, CHAT_ID));

    assertTrue(thrown.getMessage().contains("Unsupported button type"));

    verify(keyboardBuilder, never()).buildInlineKeyboard(any());
    verify(keyboardBuilder, never()).buildReplyKeyboard(any());
    verify(client, never()).execute(any(SendMessage.class));
  }

  @Test
  void
      execute_shouldThrowTelegramMessageSendExceptionAndLogError_whenTelegramClientThrowsException()
          throws TelegramApiException {
    List<Button> buttons = List.of(button);
    DialogNode node = new DialogNode(null, NODE_MESSAGE, ButtonType.INLINE, buttons);

    InlineKeyboardMarkup expectedInlineMarkup = InlineKeyboardMarkup.builder().build();
    TelegramApiException expectedException = new TelegramApiException("API Error");

    when(keyboardBuilder.buildInlineKeyboard(buttons)).thenReturn(expectedInlineMarkup);
    when(client.execute(any(SendMessage.class))).thenThrow(expectedException);

    try (TestLogCaptor logCaptor = new TestLogCaptor(DialogKeyboardExecutor.class)) {
      TelegramMessageSendException thrown =
          assertThrows(
              TelegramMessageSendException.class,
              () -> dialogKeyboardExecutor.execute(node, CHAT_ID));

      verify(keyboardBuilder).buildInlineKeyboard(buttons);
      verify(client).execute(sendMessageCaptor.capture());

      assertInstanceOf(TelegramApiException.class, thrown.getCause());

      ILoggingEvent loggedEvent = logCaptor.events().getFirst();
      assertEquals(Level.ERROR, loggedEvent.getLevel());
      assertEquals(
          "Failed to send dialog keyboard markup in chat. ChatID={}".replace("{}", CHAT_ID),
          loggedEvent.getFormattedMessage());
    }
  }
}
