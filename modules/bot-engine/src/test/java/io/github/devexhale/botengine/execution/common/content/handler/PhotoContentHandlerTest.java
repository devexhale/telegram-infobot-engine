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
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@ExtendWith(MockitoExtension.class)
class PhotoContentHandlerTest {

  private static final String CHAT_ID = "123456789";
  private static final String FILE_NAME = "test_photo.jpg";
  private static final String CAPTION = "Here is a photo!";

  @Mock private TelegramClient client;
  @Mock private MediaFileLoader mediaFileLoader;

  @InjectMocks private PhotoContentHandler photoContentHandler;

  @Mock private Message mockSentMessage;
  @Mock private InputFile inputFile;

  @Captor private ArgumentCaptor<SendPhoto> sendPhotoCaptor;

  private ContentNode contentNode;

  @BeforeEach
  void setUp() {
    contentNode = new ContentNode(ContentType.PHOTO, null, FILE_NAME, CAPTION);
  }

  @Test
  void type_returnsPhoto() {
    assertEquals(ContentType.PHOTO, photoContentHandler.type());
  }

  @Test
  void handle_shouldSendPhotoAndReturnMessage_whenSuccessful() throws TelegramApiException {
    when(mediaFileLoader.load(FILE_NAME)).thenReturn(inputFile);
    when(client.execute(any(SendPhoto.class))).thenReturn(mockSentMessage);

    Message result = photoContentHandler.handle(contentNode, CHAT_ID);

    verify(client).execute(sendPhotoCaptor.capture());
    SendPhoto capturedRequest = sendPhotoCaptor.getValue();

    assertEquals(CHAT_ID, capturedRequest.getChatId());
    assertEquals(inputFile, capturedRequest.getPhoto());
    assertEquals(CAPTION, capturedRequest.getCaption());

    assertEquals(mockSentMessage, result);
  }

  @Test
  void handle_shouldThrowTelegramMessageSendExceptionAndLogError_whenClientThrowsException()
      throws TelegramApiException {
    TelegramApiException apiException = new TelegramApiException("API Error");

    when(mediaFileLoader.load(FILE_NAME)).thenReturn(inputFile);
    when(client.execute(any(SendPhoto.class))).thenThrow(apiException);

    try (TestLogCaptor logCaptor = new TestLogCaptor(PhotoContentHandler.class)) {
      TelegramMessageSendException thrown =
          assertThrows(
              TelegramMessageSendException.class,
              () -> photoContentHandler.handle(contentNode, CHAT_ID));

      verify(client).execute(sendPhotoCaptor.capture());

      assertInstanceOf(TelegramApiException.class, thrown.getCause());

      ILoggingEvent loggedEvent = logCaptor.events().getFirst();
      assertEquals(Level.ERROR, loggedEvent.getLevel());
      assertEquals(
          "Failed to send photo in chat. ChatID={}".replace("{}", CHAT_ID),
          loggedEvent.getFormattedMessage());
    }
  }
}
