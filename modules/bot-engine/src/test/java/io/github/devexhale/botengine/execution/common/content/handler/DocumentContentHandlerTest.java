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
import io.github.devexhale.botengine.loader.MediaFileLoader;
import io.github.devexhale.botengine.testsupport.logger.TestLogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@ExtendWith(MockitoExtension.class)
class DocumentContentHandlerTest {

  private static final String CHAT_ID = "123456789";
  private static final String FILE_NAME = "test_document.pdf";
  private static final String CAPTION = "Here is the document.";

  @Mock private TelegramClient client;
  @Mock private MediaFileLoader mediaFileLoader;

  @InjectMocks private DocumentContentHandler documentContentHandler;

  @Mock private Message mockSentMessage;
  @Mock private InputFile inputFile;

  @Captor private ArgumentCaptor<SendDocument> sendDocumentCaptor;

  private ContentNode contentNode;

  @BeforeEach
  void setUp() {
    contentNode = new ContentNode(ContentType.DOCUMENT, null, FILE_NAME, CAPTION);
  }

  @Test
  void type_returnsDocument() {
    assertEquals(ContentType.DOCUMENT, documentContentHandler.type());
  }

  @Test
  void handle_shouldSendDocumentAndReturnMessage_whenSuccessful() throws TelegramApiException {
    when(mediaFileLoader.load(FILE_NAME)).thenReturn(inputFile);
    when(client.execute(any(SendDocument.class))).thenReturn(mockSentMessage);

    Message result = documentContentHandler.handle(contentNode, CHAT_ID);

    verify(client).execute(sendDocumentCaptor.capture());
    SendDocument capturedRequest = sendDocumentCaptor.getValue();

    assertEquals(CHAT_ID, capturedRequest.getChatId());
    assertEquals(inputFile, capturedRequest.getDocument());
    assertEquals(CAPTION, capturedRequest.getCaption());

    assertEquals(mockSentMessage, result);
  }

  @Test
  void handle_shouldThrowTelegramMessageSendExceptionAndLogError_whenClientThrowsException()
      throws TelegramApiException {
    TelegramApiException apiException = new TelegramApiException("API Error");

    when(mediaFileLoader.load(FILE_NAME)).thenReturn(inputFile);
    when(client.execute(any(SendDocument.class))).thenThrow(apiException);

    try (TestLogCaptor logCaptor = new TestLogCaptor(DocumentContentHandler.class)) {
      TelegramMessageSendException thrown =
          assertThrows(
              TelegramMessageSendException.class,
              () -> documentContentHandler.handle(contentNode, CHAT_ID));

      verify(client).execute(sendDocumentCaptor.capture());

      assertInstanceOf(TelegramApiException.class, thrown.getCause());

      ILoggingEvent loggedEvent = logCaptor.events().getFirst();
      assertEquals(Level.ERROR, loggedEvent.getLevel());
      assertEquals(
          "Failed to send document in chat. ChatID={}".replace("{}", CHAT_ID),
          loggedEvent.getFormattedMessage());
    }
  }
}
