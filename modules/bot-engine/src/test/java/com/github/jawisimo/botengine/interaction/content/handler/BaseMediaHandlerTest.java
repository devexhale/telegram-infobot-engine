package com.github.jawisimo.botengine.interaction.content.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

import com.github.jawisimo.botengine.model.ContentNode;
import com.github.jawisimo.botengine.model.ContentType;
import com.github.jawisimo.botengine.model.Media;
import com.github.jawisimo.botengine.loader.MediaFileLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@ExtendWith(MockitoExtension.class)
abstract class BaseMediaHandlerTest<T extends AbstractMediaHandler> {

  protected static final String CHAT_ID = "123456789";
  protected static final String FILE_NAME = "test-file.mp3";
  protected static final String CAPTION = "Test Caption";
  protected static final InputFile MEDIA_FILE = new InputFile("testFile");
  protected static final String EXCEPTION_MSG = "Something went wrong...";

  @Mock protected TelegramClient telegramClient;
  @Mock protected MediaFileLoader mediaFileLoader;

  protected T handler;

  protected Message expectedMsg;

  protected abstract T createHandler(TelegramClient client, MediaFileLoader mediaFileLoader);

  protected abstract String getExpectedMediaType();

  @BeforeEach
  void setUp() {
    lenient().when(mediaFileLoader.load(FILE_NAME)).thenReturn(MEDIA_FILE);
    handler = createHandler(telegramClient, mediaFileLoader);

    expectedMsg = new Message();
  }

  protected ContentNode createContentNodeWithExpectedMediaType(String caption) {
    Media media = new Media(getExpectedMediaType(), FILE_NAME, caption);
    return new ContentNode(ContentType.MEDIA, null, media);
  }

  @Test
  void canHandle_shouldReturnTrue_whenContentTypeIsMediaAndMediaTypeMatchesIgnoringCase() {
    ContentNode contentNode = createContentNodeWithExpectedMediaType(CAPTION);

    boolean result = handler.canHandle(contentNode);

    assertTrue(result);
  }

  @Test
  void canHandle_shouldReturnFalse_whenContentTypeIsNotMedia() {
    ContentNode expected = createContentNodeWithExpectedMediaType(CAPTION);
    Media media = expected.media();
    ContentNode notMediaNode = new ContentNode(ContentType.TEXT, expected.text(), media);

    boolean result = handler.canHandle(notMediaNode);

    assertFalse(result);
  }

  @Test
  void canHandle_shouldReturnFalse_whenMediaTypeDoesNotMatch() {
    Media media = new Media("some-other-type", FILE_NAME, CAPTION);
    ContentNode contentNode = new ContentNode(ContentType.MEDIA, null, media);

    boolean result = handler.canHandle(contentNode);

    assertFalse(result);
  }
}
