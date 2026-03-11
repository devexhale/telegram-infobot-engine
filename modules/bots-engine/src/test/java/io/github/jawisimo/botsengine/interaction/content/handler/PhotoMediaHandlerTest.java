package io.github.jawisimo.botsengine.interaction.content.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.github.jawisimo.botsengine.model.ContentNode;
import io.github.jawisimo.botsengine.loader.MediaFileLoader;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

class PhotoMediaHandlerTest extends BaseMediaHandlerTest<PhotoMediaHandler> {

  private static final String MEDIA_TYPE = "PHOTO";

  @Override
  protected PhotoMediaHandler createHandler(
      TelegramClient client, MediaFileLoader mediaFileLoader) {
    return new PhotoMediaHandler(client, mediaFileLoader);
  }

  @Override
  protected String getExpectedMediaType() {
    return MEDIA_TYPE;
  }

  @Test
  void handle_shouldSendPhotoSuccessfully() throws TelegramApiException {
    ContentNode contentNode = createContentNodeWithExpectedMediaType(CAPTION);

    when(telegramClient.execute(any(SendPhoto.class))).thenReturn(expectedMsg);

    Message result = handler.handle(contentNode, CHAT_ID);

    assertNotNull(result);
    assertEquals(expectedMsg, result);
    verify(mediaFileLoader).load(FILE_NAME);
    verify(telegramClient).execute(any(SendPhoto.class));
  }

  @Test
  void handle_shouldReturnNull_whenTelegramApiExceptionOccurs() throws TelegramApiException {
    ContentNode contentNode = createContentNodeWithExpectedMediaType(CAPTION);
    when(telegramClient.execute(any(SendPhoto.class)))
        .thenThrow(new TelegramApiException(EXCEPTION_MSG));

    Message result = handler.handle(contentNode, CHAT_ID);

    assertNull(result);
  }

  @Test
  void handle_shouldHandleSuccessfully_whenCaptionIsNull() throws TelegramApiException {
    ContentNode contentNode = createContentNodeWithExpectedMediaType(null);

    when(telegramClient.execute(any(SendPhoto.class))).thenReturn(expectedMsg);

    Message result = handler.handle(contentNode, CHAT_ID);

    assertNotNull(result);
    assertEquals(expectedMsg, result);
  }

  @Test
  void getMediaType_shouldReturnCorrectType() {
    assertEquals(MEDIA_TYPE, handler.getMediaType());
  }
}
