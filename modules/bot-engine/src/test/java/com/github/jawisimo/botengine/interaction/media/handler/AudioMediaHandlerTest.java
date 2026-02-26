package com.github.jawisimo.botengine.interaction.media.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.github.jawisimo.botengine.interaction.node.model.ContentNode;
import com.github.jawisimo.botengine.interaction.node.model.ContentType;
import com.github.jawisimo.botengine.interaction.node.model.Media;
import com.github.jawisimo.botengine.loader.MediaFileLoader;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

class AudioMediaHandlerTest extends BaseMediaHandlerTest<AudioMediaHandler> {

  @Override
  protected AudioMediaHandler createHandler(
      TelegramClient client, MediaFileLoader mediaFileLoader) {
    return new AudioMediaHandler(client, mediaFileLoader);
  }

  @Override
  protected ContentNode createContentNodeWithMedia(String caption) {
    Media media = new Media("audio", FILE_NAME, caption);
    return new ContentNode(ContentType.MEDIA, null, media);
  }

  @Test
  void handle_shouldSendAudioSuccessfully() throws TelegramApiException {
    ContentNode contentNode = createContentNodeWithMedia(CAPTION);
    Message expectedMessage = new Message();

    when(telegramClient.execute(any(SendAudio.class))).thenReturn(expectedMessage);

    Message result = handler.handle(contentNode, CHAT_ID);

    assertNotNull(result);
    assertEquals(expectedMessage, result);
    verify(mediaFileLoader).load(FILE_NAME);
    verify(telegramClient).execute(any(SendAudio.class));
  }

  @Test
  void handle_shouldReturnNull_whenTelegramApiExceptionOccurs() throws TelegramApiException {
    ContentNode contentNode = createContentNodeWithMedia(CAPTION);

    when(telegramClient.execute(any(SendAudio.class)))
        .thenThrow(new TelegramApiException(EXCEPTION_MSG));

    Message result = handler.handle(contentNode, CHAT_ID);

    assertNull(result);
  }

  @Test
  void handle_shouldHandleSuccessfully_whenCaptionIsNull() throws TelegramApiException {
    ContentNode contentNode = createContentNodeWithMedia(null);
    Message expectedMessage = new Message();

    when(telegramClient.execute(any(SendAudio.class))).thenReturn(expectedMessage);

    Message result = handler.handle(contentNode, CHAT_ID);

    assertNotNull(result);
    assertEquals(expectedMessage, result);
  }

  @Test
  void getMediaType_shouldReturnCorrectType() {
    assertEquals("AUDIO", handler.getMediaType());
  }
}
