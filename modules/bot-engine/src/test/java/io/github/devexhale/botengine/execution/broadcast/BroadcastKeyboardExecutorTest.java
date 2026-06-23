package io.github.devexhale.botengine.execution.broadcast;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.devexhale.botengine.diagnostics.exception.TelegramMessageSendException;
import io.github.devexhale.botengine.domain.broadcast.BroadcastNode;
import io.github.devexhale.botengine.execution.common.command.commandset.LastCommand;
import io.github.devexhale.botengine.testsupport.logger.TestLogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BroadcastKeyboardExecutorTest {

  private static final String CHAT_ID = "123456789";
  private static final String BROADCAST_MESSAGE = "Broadcast message text";
  private static final String RETURN_BUTTON_LABEL = "Go Back";

  @Mock private TelegramClient client;
  @InjectMocks private BroadcastKeyboardExecutor broadcastKeyboardExecutor;
  @Mock private BroadcastNode broadcastNode;

  @Captor private ArgumentCaptor<SendMessage> sendMessageCaptor;

  @BeforeEach
  void setUp() {
    lenient().when(broadcastNode.message()).thenReturn(BROADCAST_MESSAGE);
    lenient().when(broadcastNode.returnButtonLabel()).thenReturn(RETURN_BUTTON_LABEL);
  }

  @Test
  void execute_shouldSendMessageWithReturnButton_whenSuccessful() throws TelegramApiException {
    when(client.execute(any(SendMessage.class))).thenReturn(null);

    broadcastKeyboardExecutor.execute(broadcastNode, CHAT_ID);

    verify(client).execute(sendMessageCaptor.capture());
    SendMessage captured = sendMessageCaptor.getValue();

    assertEquals(CHAT_ID, captured.getChatId());
    assertEquals(BROADCAST_MESSAGE, captured.getText());

    InlineKeyboardMarkup markup = (InlineKeyboardMarkup) captured.getReplyMarkup();
    assertEquals(1, markup.getKeyboard().size());
    assertEquals(1, markup.getKeyboard().getFirst().size());

    InlineKeyboardButton button = markup.getKeyboard().getFirst().getFirst();
    assertEquals(RETURN_BUTTON_LABEL, button.getText());
    assertEquals(LastCommand.COMMAND_NAME, button.getCallbackData());
  }

  @Test
  void execute_shouldThrowTelegramMessageSendExceptionAndLogError_whenClientThrowsException()
      throws TelegramApiException {
    TelegramApiException apiException = new TelegramApiException("API fail");

    when(client.execute(any(SendMessage.class))).thenThrow(apiException);

    try (TestLogCaptor logCaptor = new TestLogCaptor(BroadcastKeyboardExecutor.class)) {
      TelegramMessageSendException thrown =
          assertThrows(
              TelegramMessageSendException.class,
              () -> broadcastKeyboardExecutor.execute(broadcastNode, CHAT_ID));

      assertInstanceOf(TelegramApiException.class, thrown.getCause());

      ILoggingEvent loggedEvent = logCaptor.events().getFirst();
      assertEquals(Level.ERROR, loggedEvent.getLevel());
      assertTrue(
          loggedEvent
              .getFormattedMessage()
              .contains("Failed to send broadcast keyboard markup in chat"));
      assertTrue(loggedEvent.getFormattedMessage().contains(CHAT_ID));
    }
  }
}
