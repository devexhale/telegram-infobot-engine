package com.github.jawisimo.botengine.interaction.content.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.github.jawisimo.botengine.interaction.node.model.ContentNode;
import com.github.jawisimo.botengine.interaction.node.model.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

class TextContentHandlerTest {

  private static final String CHAT_ID = "12345";
  private static final String TEXT = "Hello world";
  private static final String EXCEPTION_MSG = "Some error...";

  private TelegramClient telegramClient;
  private TextContentHandler handler;

  @BeforeEach
  void setUp() {
    telegramClient = mock(TelegramClient.class);
    handler = new TextContentHandler(telegramClient);
  }

  @Test
  void canHandle_shouldReturnTrue_whenContentTypeIsText() {
    ContentNode contentNode = createTextContentNode();

    assertTrue(handler.canHandle(contentNode));
  }

  @Test
  void canHandle_shouldReturnFalse_whenContentTypeIsNotText() {
    ContentNode contentNode = new ContentNode(ContentType.MEDIA, null, null);

    assertFalse(handler.canHandle(contentNode));
  }

  @Test
  void handle_shouldSendMessageSuccessfully() throws TelegramApiException {
    ContentNode contentNode = createTextContentNode();
    Message expectedMessage = new Message();

    when(telegramClient.execute(any(SendMessage.class))).thenReturn(expectedMessage);

    Message result = handler.handle(contentNode, CHAT_ID);

    assertNotNull(result);
    assertEquals(expectedMessage, result);
    verify(telegramClient).execute(any(SendMessage.class));
  }

  @Test
  void handle_shouldReturnNull_whenTelegramApiExceptionOccurs() throws TelegramApiException {
    ContentNode contentNode = createTextContentNode();

    when(telegramClient.execute(any(SendMessage.class)))
        .thenThrow(new TelegramApiException(EXCEPTION_MSG));

    Message result = handler.handle(contentNode, CHAT_ID);

    assertNull(result);
    verify(telegramClient).execute(any(SendMessage.class));
  }

  private ContentNode createTextContentNode() {
    return new ContentNode(ContentType.TEXT, TEXT, null);
  }
}
