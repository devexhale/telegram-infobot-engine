package io.github.devexhale.botengine.execution.common.content.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.devexhale.botengine.diagnostics.exception.TelegramMessageSendException;
import io.github.devexhale.botengine.domain.content.ContentNode;
import io.github.devexhale.botengine.domain.content.ContentType;
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
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@ExtendWith(MockitoExtension.class)
class TextContentHandlerTest {

  private static final String CHAT_ID = "123456789";
  private static final String TEXT_CONTENT = "Hello, world!";

  @Mock private TelegramClient client;

  @InjectMocks private TextContentHandler textContentHandler;

  @Mock private Message mockSentMessage;

  @Captor private ArgumentCaptor<SendMessage> sendMessageCaptor;

  private ContentNode contentNode;

  @BeforeEach
  void setUp() {
    contentNode = new ContentNode(ContentType.TEXT, TEXT_CONTENT, null, null);
  }

  @Test
  void type_returnsText() {
    assertEquals(ContentType.TEXT, textContentHandler.type());
  }

  @Test
  void handle_shouldSendTextAndReturnMessage_whenSuccessful() throws TelegramApiException {
    when(client.execute(any(SendMessage.class))).thenReturn(mockSentMessage);

    Message result = textContentHandler.handle(contentNode, CHAT_ID);

    verify(client).execute(sendMessageCaptor.capture());
    SendMessage capturedRequest = sendMessageCaptor.getValue();

    assertEquals(CHAT_ID, capturedRequest.getChatId());
    assertEquals(TEXT_CONTENT, capturedRequest.getText());

    assertEquals(mockSentMessage, result);
  }

  @Test
  void handle_shouldThrowTelegramMessageSendExceptionAndLogError_whenClientThrowsException()
      throws TelegramApiException {
    TelegramApiException apiException = new TelegramApiException("API Error");

    when(client.execute(any(SendMessage.class))).thenThrow(apiException);

    try (TestLogCaptor logCaptor = new TestLogCaptor(TextContentHandler.class)) {
      TelegramMessageSendException thrown =
          assertThrows(
              TelegramMessageSendException.class,
              () -> textContentHandler.handle(contentNode, CHAT_ID));

      verify(client).execute(sendMessageCaptor.capture());

      assertInstanceOf(TelegramApiException.class, thrown.getCause());

      ILoggingEvent loggedEvent = logCaptor.events().getFirst();
      assertEquals(Level.ERROR, loggedEvent.getLevel());
      assertEquals(
          "Failed to send text content in chat. ChatID={}".replace("{}", CHAT_ID),
          loggedEvent.getFormattedMessage());
    }
  }
}
