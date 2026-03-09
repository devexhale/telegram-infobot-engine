package com.github.jawisimo.botengine.interaction.content.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.github.jawisimo.botengine.model.ContentNode;
import com.github.jawisimo.botengine.loader.MediaFileLoader;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

class DocumentMediaHandlerTest extends BaseMediaHandlerTest<DocumentMediaHandler> {

  private static final String MEDIA_TYPE = "DOCUMENT";

  @Override
  protected DocumentMediaHandler createHandler(
      TelegramClient client, MediaFileLoader mediaFileLoader) {
    return new DocumentMediaHandler(client, mediaFileLoader);
  }

  @Override
  protected String getExpectedMediaType() {
    return MEDIA_TYPE;
  }

  @Test
  void handle_shouldSendDocumentSuccessfully() throws TelegramApiException {
    ContentNode contentNode = createContentNodeWithExpectedMediaType(CAPTION);

    when(telegramClient.execute(any(SendDocument.class))).thenReturn(expectedMsg);

    Message result = handler.handle(contentNode, CHAT_ID);

    assertNotNull(result);
    assertEquals(expectedMsg, result);
    verify(mediaFileLoader).load(FILE_NAME);
    verify(telegramClient).execute(any(SendDocument.class));
  }

  @Test
  void handle_shouldReturnNull_whenTelegramApiExceptionOccurs() throws TelegramApiException {
    ContentNode contentNode = createContentNodeWithExpectedMediaType(CAPTION);
    when(telegramClient.execute(any(SendDocument.class)))
        .thenThrow(new TelegramApiException(EXCEPTION_MSG));

    Message result = handler.handle(contentNode, CHAT_ID);

    assertNull(result);
  }

  @Test
  void handle_shouldHandleSuccessfully_whenCaptionIsNull() throws TelegramApiException {
    ContentNode contentNode = createContentNodeWithExpectedMediaType(null);
    Message expectedMsg = new Message();

    when(telegramClient.execute(any(SendDocument.class))).thenReturn(expectedMsg);

    Message result = handler.handle(contentNode, CHAT_ID);

    assertNotNull(result);
    assertEquals(expectedMsg, result);
  }

  @Test
  void getMediaType_shouldReturnCorrectType() {
    assertEquals(MEDIA_TYPE, handler.getMediaType());
  }
}
